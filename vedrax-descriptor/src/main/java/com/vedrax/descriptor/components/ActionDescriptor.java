package com.vedrax.descriptor.components;

import com.vedrax.descriptor.enums.ActionType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The class that represents an action
 */
@Data
@NoArgsConstructor
public class ActionDescriptor {
  private String label;
  private ActionType action;
  private String url;
  private String fragment;
}
