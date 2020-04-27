package com.vedrax.descriptor.annotations;

public @interface Endpoint {
  String key();//can be of the form parentKey:childKey
  String url();
}
