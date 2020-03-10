package com.vedrax.descriptor.typeof;

public class TypeOf {

  public static <S> WhenTypeOf<S> whenTypeOf(S object) {
    return new WhenTypeOf<>(object);
  }
}
