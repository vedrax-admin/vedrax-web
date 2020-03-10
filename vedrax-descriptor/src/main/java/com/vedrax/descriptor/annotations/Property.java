package com.vedrax.descriptor.annotations;

/**
 * Annotation for describing a component property
 */
public @interface Property {
  String propertyName();
  String propertyValue();
}
