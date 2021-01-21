package com.vedrax.gcp;

import java.util.Map;

public interface GCPTaskService {

    void createDefaultTask(String uri, Map<String, String> params);

    void createTaskWithName(String taskId,
                            String uri,
                            Map<String, String> params);

    void createDelayedTask(String uri,
                      int seconds,
                      Map<String, String> params);
}
