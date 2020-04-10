package com.vedrax.descriptor.components;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vedrax.descriptor.enums.ActionType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The class that represents an action
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionDescriptor {
  private String label;
  private ActionType action;
  private String url;
  private String fragment;
}
