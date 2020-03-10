package com.vedrax.descriptor.components;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents a form
 */
@Data
@NoArgsConstructor
public class FormDescriptor {

  private List<FormControlDescriptor> controls = new ArrayList<>();
  private List<FormGroupDescriptor> groups = new ArrayList<>();
  private String endpoint;
  private String method;
  private String successUrl;
  private AuditDescriptor audit;

  public boolean addControl(FormControlDescriptor formControlDescriptor){
    return controls.add(formControlDescriptor);
  }

  public boolean addGroup(FormGroupDescriptor formGroupDescriptor){
    return groups.add(formGroupDescriptor);
  }


}
