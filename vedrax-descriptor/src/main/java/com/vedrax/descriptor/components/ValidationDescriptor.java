package com.vedrax.descriptor.components;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class represents a validation descriptor
 */
@Data
@NoArgsConstructor
public class ValidationDescriptor {
  private String validationName;
  private Object validationValue;
  private String validationMessage;
}
