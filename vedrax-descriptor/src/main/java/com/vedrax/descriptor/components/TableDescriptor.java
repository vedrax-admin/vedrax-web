package com.vedrax.descriptor.components;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents a table descriptor
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableDescriptor {

  private String title;
  private Boolean paginated;
  private Boolean loadOnInit;
  private String path;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<FormControlDescriptor> searchControls = new ArrayList<>();
  private List<ColumnDescriptor> columns = new ArrayList<>();
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<?> values = new ArrayList<>();
}
