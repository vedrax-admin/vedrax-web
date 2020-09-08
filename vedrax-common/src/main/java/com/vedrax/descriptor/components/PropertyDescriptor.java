package com.vedrax.descriptor.components;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class represents a property descriptor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDescriptor {
  private String propertyName;
  private Object propertyValue;
}
