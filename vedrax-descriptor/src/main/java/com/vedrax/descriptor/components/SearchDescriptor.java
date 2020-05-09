package com.vedrax.descriptor.components;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchDescriptor {
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<FormControlDescriptor> searchControls = new ArrayList<>();
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<EndpointDescriptor> lovs = new ArrayList<>();
}
