package com.vedrax.descriptor.components;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DescriptorParameter {
  private Class<?> dto;
  private String url;
}
