package com.vedrax.descriptor;

import com.vedrax.descriptor.lov.EnumWithValue;

public enum UserRole implements EnumWithValue {

  ADMIN("ADMIN"),
  USER("USER");

  private String value;

  UserRole(String value){
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }
}
