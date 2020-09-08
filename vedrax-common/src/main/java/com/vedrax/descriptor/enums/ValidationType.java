package com.vedrax.descriptor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enums represents the validation type available
 */
@AllArgsConstructor
public enum ValidationType {
  required("required"),
  maxlength("maxlength"),
  minlength("minlength"),
  max("max"),
  min("min"),
  pattern("pattern"),
  email("email");

  @Getter
  private String name;

}
