package com.vedrax.gcp;

public class GCPFactoryImpl implements GCPFactory {

    public GCPFactoryImpl() {
    }

    @Override
    public GCPFetchService createFetchService(String projectId, String locationId) {
        return new GCPFetch(projectId, locationId);
    }

    @Override
    public GCPStorage createStorageService(String projectId, String bucketName) {
        return new GCPStorage(projectId, bucketName);
    }

    @Override
    public GCPTaskService createTaskService(String projectId, String locationId) {
        return new GCPTask(projectId, locationId);
    }

    @Override
    public GCPTaskService createTaskService(String projectId, String locationId, String queueId) {
        return new GCPTask(projectId, locationId, queueId);
    }


}
