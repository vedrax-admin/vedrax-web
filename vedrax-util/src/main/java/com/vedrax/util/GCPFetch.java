package com.vedrax.util;

import org.apache.commons.lang3.Validate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GCPFetch implements GCPFetchService {

    private final Logger LOG = Logger.getLogger(GCPFetch.class.getName());

    private final String projectId;
    private final String locationId;

    public GCPFetch(String projectId, String locationId) {
        Objects.requireNonNull(projectId, "projectId must be provided");
        Objects.requireNonNull(locationId, "locationId must be provided");

        this.projectId = projectId;
        this.locationId = locationId;
    }

    @Override
    public Optional<String> fetchURL(String path) {
        Validate.notNull(path, "path must be provided");

        String urlString = String.format("http://%s.%s.r.appspot.com/%s", projectId, locationId, path);

        try {

            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Enable output for the connection.
            conn.setDoOutput(true);
            conn.setConnectTimeout(480000);//8 minutes timeout
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            // Set HTTP request method.
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                String line;

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return Optional.of(response.toString());
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, String.format("Error fetching the resource [%s]", urlString), ex);
        }

        return Optional.empty();

    }


}
