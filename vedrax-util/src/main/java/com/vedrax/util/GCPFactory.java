package com.vedrax.util;

public interface GCPFactory {

    GCPFetchService createFetchService(String projectId, String locationId);

    GCPStorageService createStorageService(String projectId, String bucketName);

    GCPTaskService createTaskService(String projectId, String locationId);

    GCPTaskService createTaskService(String projectId, String locationId, String queueId);
}
