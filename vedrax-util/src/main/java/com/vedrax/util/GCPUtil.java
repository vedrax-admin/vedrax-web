package com.vedrax.util;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.urlfetch.ResponseTooLargeException;
import org.apache.commons.lang3.Validate;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class GCPUtil {

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
}
