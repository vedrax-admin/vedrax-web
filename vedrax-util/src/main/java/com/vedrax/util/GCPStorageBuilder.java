package com.vedrax.util;

import com.google.cloud.storage.*;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class GCPStorageBuilder {

    private final String bucketName;
    private final Storage storage;

    public GCPStorageBuilder(String projectId, String bucketName) {
        Objects.requireNonNull(projectId, "projectId must be provided");
        Objects.requireNonNull(bucketName, "bucketName must be provided");

        this.bucketName = bucketName;
        this.storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    }

    public void uploadFile(String objectName, byte[] input)
            throws IOException {
        Validate.notNull(objectName, "objectName must be provided");
        Validate.isTrue(input != null && input.length > 0, "input must be provided");

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, input);
    }

    public void downloadFile(String objectName, OutputStream output) {
        Validate.notNull(objectName, "objectName must be provided");
        Validate.notNull(output, "output must be provided");

        Blob blob = storage.get(BlobId.of(bucketName, objectName));
        blob.downloadTo(output);
    }


}
