package com.vedrax.descriptor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FormDto {

  private Class<?> dto;
  private String endpoint;
  private String method;
  private Object source;


}
