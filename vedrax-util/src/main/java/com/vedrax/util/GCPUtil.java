package com.vedrax.util;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.urlfetch.ResponseTooLargeException;
import com.google.appengine.tools.cloudstorage.*;
import org.apache.commons.lang3.Validate;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GCPUtil {

    private static final Logger LOG = Logger.getLogger(GCPUtil.class.getName());
    public static final boolean SERVE_USING_BLOBSTORE_API = false;

    /**
     * This is where backoff parameters are configured. Here it is aggressively retrying with
     * backoff, up to 10 times but taking no more that 15 seconds total to do so.
     */
    private static final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());

    /**
     * Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB
     */
    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    public static void runTaskWithParams(String endpoint, Map<String, String> params, String securityToken) {
        Validate.notNull(endpoint, "endpoint must be provided");
        Validate.notEmpty(params, "params must be provided");
        Validate.notEmpty(securityToken, "securityToken must be provided");

        String authHeaderValue = String.format("Bearer %s", securityToken);

        Queue queue = QueueFactory.getDefaultQueue();
        TaskOptions options = TaskOptions.Builder.withUrl(endpoint).retryOptions(
                RetryOptions.Builder.withTaskRetryLimit(10)
        ).header("Authorization", authHeaderValue);
        params.forEach(options::param);
        queue.add(options);
    }

    public static String fetchModuleURL(String moduleId, String path) {
        Validate.notNull(moduleId, "moduleId must be provided");
        Validate.notNull(path, "path must be provided");

        ModulesService modulesService = ModulesServiceFactory.getModulesService();

        try {
            URL url = new URL("http://" + modulesService.getVersionHostname(moduleId, null) + "/" + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Enable output for the connection.
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");

            int respCode = conn.getResponseCode(); // New items get NOT_FOUND on PUT
            if (respCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                String line;

                // Read input data stream.
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                throw new IllegalArgumentException("Cannot access resource at [" + path + "] with errors [" + conn.getResponseCode() + "] - " + conn.getResponseMessage());
            }
        } catch (ResponseTooLargeException | IOException ex) {
            throw new IllegalArgumentException("cannot fetch resources from the URL: " + path, ex);
        }
    }

    public static void readingFromCloudStorage(HttpServletResponse resp, String bucketName, String objectName) {
        try {
            GcsFilename fileName = new GcsFilename(bucketName, objectName);
            if (SERVE_USING_BLOBSTORE_API) {
                BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
                BlobKey blobKey = blobstoreService.createGsBlobKey(fileName.getBucketName() + "/" + fileName.getObjectName());
                blobstoreService.serve(blobKey, resp);
            } else {
                GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
                copy(Channels.newInputStream(readChannel), resp.getOutputStream());
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, String.format("Cannot read file [%s] to bucket [%s]", objectName, bucketName), ex);
        }
    }

    public static void writingToCloudStorage(InputStream input, String bucketName, String objectName) {
        Validate.notNull(input, "input stream must be provided");
        Validate.notNull(bucketName, "bucketName must be provided");
        Validate.notNull(objectName, "objectName must be provided");

        try {
            GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
            GcsFilename fileName = new GcsFilename(bucketName, objectName);
            GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName, instance);
            copy(input, Channels.newOutputStream(outputChannel));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, String.format("Cannot write file [%s] to bucket [%s]", objectName, bucketName), ex);
        }
    }

    /**
     * Transfer the data from the inputStream to the outputStream. Then close both streams.
     */
    private static void copy(InputStream input, OutputStream output) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
        } finally {
            input.close();
            output.close();
        }
    }
}
