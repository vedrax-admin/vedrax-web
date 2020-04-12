package com.vedrax.security.enveloppe;

public class ResponseWrapperUtil {

  public static ResponseWrapper initSuccessWrapper(Object object, String message){
    Status status = new Status();
    status.setError(false);
    status.setCode(200);
    status.setType("Success");
    status.setMessage(message);

    ResponseWrapper responseWrapper = new ResponseWrapper();
    responseWrapper.setStatus(status);
    responseWrapper.setData(object);
    return responseWrapper;
  }

}
