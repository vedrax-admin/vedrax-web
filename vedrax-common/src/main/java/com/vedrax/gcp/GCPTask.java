package com.vedrax.gcp;

import com.google.cloud.tasks.v2.*;

import com.google.protobuf.ByteString;
import com.vedrax.security.TokenUtility;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.vedrax.util.JsonUtils.mapToJson;

public class GCPTask implements GCPTaskService {

    private final Logger LOG = Logger.getLogger(GCPTask.class.getName());

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final String projectId;
    private final String locationId;
    private String queueId = "default";

    public GCPTask(String projectId, String locationId) {
        Objects.requireNonNull(projectId, "projectId must be provided");
        Objects.requireNonNull(locationId, "locationId must be provided");

        this.projectId = projectId;
        this.locationId = locationId;
    }

    public GCPTask(String projectId, String locationId, String queueId) {
        this(projectId, locationId);

        Objects.requireNonNull(queueId, "queue ID must be provided");
        this.queueId = queueId;
    }

    @Override
    public void createDefaultTask(String uri, Map<String, String> params) {
        Validate.notNull(uri, "uri must be provided");

        // Construct the fully qualified queue name.
        String fullQualifiedQueueName = getFullQualifiedQueueName();

        try (CloudTasksClient client = CloudTasksClient.create()) {

            // Construct the task body.
            Task task = Task.newBuilder()
                    .setAppEngineHttpRequest(
                            AppEngineHttpRequest.newBuilder()
                                    .putHeaders(AUTHORIZATION_HEADER, String.format("Bearer %s", TokenUtility.getAdminToken()))
                                    .setRelativeUri(uri)
                                    .setBody(ByteString.copyFromUtf8(mapToJson(params)))
                                    .setHttpMethod(HttpMethod.POST)
                                    .build())
                    .build();

            // Add the task to the default queue.
            Task taskResponse = client.createTask(fullQualifiedQueueName, task);

            LOG.log(Level.INFO, String.format("Task with name [%s] initiated...", taskResponse.getName()));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "A task initiation has failed", ex);
        }
    }

    @Override
    public void createTaskWithName(String taskId,
                                   String uri,
                                   Map<String, String> params) {
        Validate.notNull(taskId, "Task ID must be provided");
        Validate.notNull(uri, "uri must be provided");

        // Construct the fully qualified queue name.
        String fullQualifiedQueueName = getFullQualifiedQueueName();

        // Construct the fully qualified task name.
        String taskName = String.format("%s/tasks/%s", fullQualifiedQueueName, taskId);

        try (CloudTasksClient client = CloudTasksClient.create()) {

            // Construct the task body.
            Task task = Task.newBuilder()
                    .setName(taskName)
                    .setAppEngineHttpRequest(
                            AppEngineHttpRequest.newBuilder()
                                    .putHeaders(AUTHORIZATION_HEADER, String.format("Bearer %s", TokenUtility.getAdminToken()))
                                    .setRelativeUri(uri)
                                    .setBody(ByteString.copyFromUtf8(mapToJson(params)))
                                    .setHttpMethod(HttpMethod.POST)
                                    .build())
                    .build();

            // Add the task to the default queue.
            Task taskResponse = client.createTask(fullQualifiedQueueName, task);

            LOG.log(Level.INFO, String.format("Task with name [%s] initiated...", taskResponse.getName()));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "A task initiation has failed", ex);
        }
    }

    private String getFullQualifiedQueueName() {

        return QueueName.of(projectId, locationId, queueId).toString();
    }

}
