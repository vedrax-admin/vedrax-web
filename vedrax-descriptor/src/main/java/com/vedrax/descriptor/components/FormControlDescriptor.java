package com.vedrax.descriptor.components;

import com.vedrax.descriptor.lov.NVP;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * The class that represents a form control
 */
@Data
@NoArgsConstructor
public class FormControlDescriptor {

  private String controlName;
  private List<PropertyDescriptor> controlProperties = new ArrayList<>();
  private String controlLabel;
  private String controlType;
  private String controlHint;
  private Object controlValue;
  private List<ValidationDescriptor> controlValidations = new ArrayList<>();
  private List<NVP> controlOptions = new ArrayList<>();
  private List<FormControlDescriptor> controlChildren = new ArrayList<>();
  private String controlDisplayKey;
  private String controlDisplayValue;
  private TableDescriptor controlSearch;

  public boolean addProperty(PropertyDescriptor propertyDescriptor) {
    return controlProperties.add(propertyDescriptor);
  }

  public boolean addValidation(ValidationDescriptor validationDescriptor) {
    return controlValidations.add(validationDescriptor);
  }

  public boolean addChildComponent(FormControlDescriptor formControlDescriptor) {
    return controlChildren.add(formControlDescriptor);
  }
}
