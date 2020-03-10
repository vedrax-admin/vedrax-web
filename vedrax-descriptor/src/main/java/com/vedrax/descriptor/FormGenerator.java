package com.vedrax.descriptor;

import com.vedrax.descriptor.components.FormDescriptor;

import java.util.Locale;

public interface FormGenerator {

  /**
   * generate form descriptor
   *
   * @param formDto the provided form params
   * @param locale  the locale
   * @return the form
   */
  FormDescriptor generate(FormDto formDto, Locale locale);

}
