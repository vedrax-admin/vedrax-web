package com.vedrax.descriptor.annotations;

/**
 * Annotation for describing a specific action at the column level
 */
public @interface Action {
  String label();
  String type() default "redirect";
  String value();
}
