package com.vedrax.gcp;

import com.google.cloud.storage.*;
import org.apache.commons.lang3.Validate;

import java.io.OutputStream;
import java.util.Objects;

public class GCPStorage implements GCPStorageService {

    private final String bucketName;
    private final Storage storage;

    public GCPStorage(String projectId, String bucketName) {
        Objects.requireNonNull(projectId, "projectId must be provided");
        Objects.requireNonNull(bucketName, "bucketName must be provided");

        this.bucketName = bucketName;
        this.storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    }

    @Override
    public void uploadFile(String objectName, byte[] input) {
        Validate.notNull(objectName, "objectName must be provided");
        Validate.isTrue(input != null && input.length > 0, "input must be provided");

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, input);
    }

    @Override
    public void downloadFile(String objectName, OutputStream output) {
        Validate.notNull(objectName, "objectName must be provided");
        Validate.notNull(output, "output must be provided");

        Blob blob = storage.get(BlobId.of(bucketName, objectName));
        blob.downloadTo(output);
    }


}
