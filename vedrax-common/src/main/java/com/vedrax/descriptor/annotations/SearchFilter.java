package com.vedrax.descriptor.annotations;

/**
 * Annotation for identifying group of properties
 */
public @interface SearchFilter {
  String controlName();
  String controlType();
  String endpoint() default "";
}
