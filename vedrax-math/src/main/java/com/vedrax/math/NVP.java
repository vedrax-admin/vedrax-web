package com.vedrax.math;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NVP {
  private String key;
  @Pattern(regexp = "^[+-]?(?=.?\\d)\\d*(\\.\\d{0,9})?$")
  private String value;
}
