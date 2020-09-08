package com.vedrax.gcp;

import java.util.Optional;

public interface GCPFetchService {

    Optional<String> fetchURL(String path);

}
