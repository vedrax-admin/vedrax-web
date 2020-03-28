package com.vedrax.descriptor.components;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A class that represents a column in a table
 */
@Data
@NoArgsConstructor
public class ColumnDescriptor {
  private String id;
  private String label;
  private List<ActionDescriptor> actions;
}
