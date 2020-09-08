package com.vedrax.descriptor.components;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a column in a table
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColumnDescriptor {
  private String id;
  private String label;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<ActionDescriptor> actions = new ArrayList<>();
}
