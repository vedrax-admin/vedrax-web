package com.vedrax.descriptor.components;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class represents option inside a select component
 */
@Data
@NoArgsConstructor
public class OptionDescriptor {
  private String key;
  private String value;
}
