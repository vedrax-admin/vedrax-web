package com.vedrax.descriptor.components;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Class represents a table descriptor
 */
@Data
@NoArgsConstructor
public class TableDescriptor {
  private String path;
  private List<FormControlDescriptor> searchControls;
  private List<ColumnDescriptor> columnDescriptors;
  private List<?> values;
}
