package com.vedrax.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class GCPFetchModuleBuilder {

    private final String projectId;
    private final String locationId;
    private final String path;
    private String securityToken;

    public GCPFetchModuleBuilder(String projectId, String locationId, String path) {
        Objects.requireNonNull(projectId, "projectId must be provided");
        Objects.requireNonNull(locationId, "locationId must be provided");
        Objects.requireNonNull(path, "path must be provided");

        this.projectId = projectId;
        this.locationId = locationId;
        this.path = path;
    }

    public GCPFetchModuleBuilder withSecurityToken(String securityToken) {
        this.securityToken = securityToken;
        return this;
    }

    public String fetchURL() throws IOException {

        String urlString = String.format("https://%s.%s.r.appspot.com/%s", projectId, locationId, path);

        URL url = new URL(urlString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Enable output for the connection.
        conn.setDoOutput(true);
        conn.setConnectTimeout(480000);//8 minutes timeout
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", String.format("Bearer %s", securityToken));
        // Set HTTP request method.
        conn.setRequestMethod("GET");

        int respCode = conn.getResponseCode();
        if (respCode == HttpURLConnection.HTTP_OK) {
            StringBuilder response = new StringBuilder();
            String line;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            throw new IllegalArgumentException("cannot fetch resources from the URL: " + path);
        }

    }


}
