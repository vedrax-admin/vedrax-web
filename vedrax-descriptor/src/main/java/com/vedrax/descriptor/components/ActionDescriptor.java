package com.vedrax.descriptor.components;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The class that represents an action
 */
@Data
@NoArgsConstructor
public class ActionDescriptor {
  private String label;
  private String type;
  private String value;
}
