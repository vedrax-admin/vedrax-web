package com.vedrax.descriptor.components;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Class represents a group of components inside a form
 */
@Data
@NoArgsConstructor
public class FormGroupDescriptor {
  private String name;
  private List<String> ids;
}
