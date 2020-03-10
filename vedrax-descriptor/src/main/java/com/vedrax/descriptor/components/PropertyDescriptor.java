package com.vedrax.descriptor.components;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class represents a property descriptor
 */
@Data
@NoArgsConstructor
public class PropertyDescriptor {
  private String propertyName;
  private Object propertyValue;
}
