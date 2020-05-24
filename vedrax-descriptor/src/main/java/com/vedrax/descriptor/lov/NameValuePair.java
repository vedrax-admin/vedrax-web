package com.vedrax.descriptor.lov;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameValuePair {
  private Long key;
  private String value;
}
