package com.vedrax.descriptor.annotations;

/**
 * Annotation for identifying group of properties
 */
public @interface Group {
  String name();
  String[] properties();
}
