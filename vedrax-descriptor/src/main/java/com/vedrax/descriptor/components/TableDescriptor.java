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

  private String title;
  private Boolean paginated;
  private String path;
  private List<FormControlDescriptor> searchControls;
  private List<ColumnDescriptor> columns;
  private List<?> values;
}
