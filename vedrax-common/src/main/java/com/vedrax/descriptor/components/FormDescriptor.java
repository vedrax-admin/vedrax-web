package com.vedrax.descriptor.components;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents a form
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormDescriptor {

  private String title;
  private List<FormControlDescriptor> controls = new ArrayList<>();
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<FormGroupDescriptor> groups = new ArrayList<>();
  private String endpoint;
  private String method;
  private Boolean multipart = false;
  private String successUrl;
  private String submitLabel;
  private String cancelLabel;
  private String successMessage;
  private Boolean updateTable;

  public boolean addGroup(FormGroupDescriptor formGroupDescriptor){
    return groups.add(formGroupDescriptor);
  }

}
