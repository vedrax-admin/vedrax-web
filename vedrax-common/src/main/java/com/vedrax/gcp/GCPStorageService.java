package com.vedrax.gcp;

import java.io.OutputStream;

public interface GCPStorageService {

    void uploadFile(String objectName, byte[] input);

    void downloadFile(String objectName, OutputStream output);
}
