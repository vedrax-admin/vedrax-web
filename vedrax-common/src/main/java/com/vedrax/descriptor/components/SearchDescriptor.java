package com.vedrax.descriptor.components;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchDescriptor {
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<FormControlDescriptor> controls;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<EndpointDescriptor> lovs;

  public SearchDescriptor() {
    this.controls = new ArrayList<>();
    this.lovs = new ArrayList<>();
  }

  public void  addControl(FormControlDescriptor ctrl){
    this.controls.add(ctrl);
  }

  public void addEndpoint(EndpointDescriptor endpoint){
    this.lovs.add(endpoint);
  }
}
