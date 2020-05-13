package com.vedrax.util;

import com.google.appengine.api.taskqueue.RetryOptions;
import org.apache.commons.lang3.Validate;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import java.util.Map;

public class GCPUtil {

  public static void runTaskWithParams(String endpoint, Map<String, String> params, String securityToken) {
    Validate.notNull(endpoint, "endpoint must be provided");
    Validate.notEmpty(params, "params must be provided");
    Validate.notEmpty(securityToken, "securityToken must be provided");

    String authHeaderValue = String.format("Bearer %s", securityToken);

    Queue queue = QueueFactory.getDefaultQueue();
    TaskOptions options = TaskOptions.Builder.withUrl(endpoint).retryOptions(
      RetryOptions.Builder.withTaskRetryLimit(10)
    ).header("Authorization", authHeaderValue);
    params.forEach(options::param);
    queue.add(options);
  }
}
