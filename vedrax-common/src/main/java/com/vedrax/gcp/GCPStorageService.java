package com.vedrax.gcp;

import com.google.cloud.storage.Blob;

import java.io.OutputStream;
import java.util.Optional;

public interface GCPStorageService {

    void uploadFile(String objectName, byte[] input);

    void downloadFile(String objectName, OutputStream output);

    Optional<Blob> getBlob(String objectName);
}
