package com.vedrax.descriptor.components;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The class that represents the param for constructing a descriptor
 */
@Data
@NoArgsConstructor
public class DescriptorParams {

  private String entity;
  private Object id;
  private String locale;

}
