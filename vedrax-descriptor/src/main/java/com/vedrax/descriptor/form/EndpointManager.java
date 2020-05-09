package com.vedrax.descriptor.form;

import com.vedrax.descriptor.annotations.Endpoint;
import com.vedrax.descriptor.annotations.Endpoints;
import com.vedrax.descriptor.components.EndpointDescriptor;
import com.vedrax.descriptor.components.FormDescriptor;
import com.vedrax.descriptor.components.SearchDescriptor;
import org.apache.commons.lang3.Validate;

import java.util.*;

public class EndpointManager {

  private Class<?> sourceClass;

  public EndpointManager(Class<?> sourceClass) {
    Objects.requireNonNull(sourceClass, "source class must be provided");

    this.sourceClass = sourceClass;
  }

  public void init(FormDescriptor formDescriptor) {
    Validate.notNull(formDescriptor,"form descriptor must be provided");

    formDescriptor.setLovs(getEndpoints());
  }

  public void init(SearchDescriptor searchDescriptor){
    Validate.notNull(searchDescriptor,"search descriptor must be provided");

    searchDescriptor.setLovs(getEndpoints());

  }

  private List<EndpointDescriptor> getEndpoints(){
    List<EndpointDescriptor> endpoints = new ArrayList<>();

    Optional<Endpoints> optionalGroups = getEndpointsAnnotation(sourceClass);
    if (optionalGroups.isPresent()) {
      for (Endpoint endpoint : optionalGroups.get().endpoints()) {
        EndpointDescriptor endpointDescriptor = createEndpoint(endpoint);
        endpoints.add(endpointDescriptor);
      }
    }
    return endpoints;
  }

  /**
   * Method for getting Groups annotation
   *
   * @param dtoClass the dto class
   * @return the optional Groups
   */
  private Optional<Endpoints> getEndpointsAnnotation(Class<?> dtoClass) {
    Endpoints annotation = dtoClass.getDeclaredAnnotation(Endpoints.class);
    return Optional.ofNullable(annotation);
  }

  /**
   * Method for creating an endpoint descriptor from annotation
   *
   * @param endpoint the group annotation
   * @return group descriptor
   */
  private EndpointDescriptor createEndpoint(Endpoint endpoint) {
    EndpointDescriptor endpointDescriptor = new EndpointDescriptor();
    endpointDescriptor.setKey(endpoint.key());
    endpointDescriptor.setUrl(endpoint.url());
    return endpointDescriptor;
  }
}
