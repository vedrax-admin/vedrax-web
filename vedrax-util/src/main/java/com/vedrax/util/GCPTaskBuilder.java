package com.vedrax.util;

import com.google.cloud.tasks.v2.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.protobuf.ByteString;

public class GCPTaskBuilder {

    private final String projectId;
    private final String locationId;
    private final String uri;
    private String securityToken;
    private Map<String, String> params = new HashMap<>();

    public GCPTaskBuilder(String projectId, String locationId, String uri) {
        Objects.requireNonNull(projectId, "projectId must be provided");
        Objects.requireNonNull(locationId, "locationId must be provided");
        Objects.requireNonNull(uri, "uri must be provided");

        this.projectId = projectId;
        this.locationId = locationId;
        this.uri = uri;
    }

    public GCPTaskBuilder withSecurityToken(String securityToken) {
        this.securityToken = securityToken;
        return this;
    }

    public GCPTaskBuilder withParameter(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public String createDefaultTask() throws IOException {

        try (CloudTasksClient client = CloudTasksClient.create()) {

            // Construct the fully qualified queue name.
            String queueName = QueueName.of(projectId, locationId, "default").toString();

            // Construct the task body.
            Task task = Task.newBuilder()
                    .setAppEngineHttpRequest(
                            AppEngineHttpRequest.newBuilder()
                                    .setRelativeUri(uri)
                                    .putHeaders("Authorization", String.format("Bearer %s", securityToken))
                                    .setBody(ByteString.copyFromUtf8(convertParamsWithStream()))
                                    .setHttpMethod(HttpMethod.POST)
                                    .build())
                    .build();

            // Add the task to the default queue.
            Task taskResponse = client.createTask(queueName, task);
            return taskResponse.getName();
        }
    }

    public String convertParamsWithStream() {
        return params.keySet().stream()
                .map(key -> key + "=" + params.get(key))
                .collect(Collectors.joining("&"));
    }

}
