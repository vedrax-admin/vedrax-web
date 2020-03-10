package com.vedrax.descriptor.typeof;

import java.util.function.Consumer;

public class ThenIs<S, T> {

  final Then<S> parent;
  private final S object;
  private final Class<T> expectedType;

  ThenIs(Then<S> parent, S object, Class<T> expectedType) {
    this.parent = parent;
    this.object = object;
    this.expectedType = expectedType;
  }

  public Then<S> then(Consumer<T> thenBlock) {
    if (matchingType()) {
      thenBlock.accept(castObject());
      return new TerminalThen<>();
    }
    return parent;
  }

  private T castObject() {
    return (T) object;
  }

  private boolean matchingType() {
    return object != null && expectedType.isAssignableFrom(object.getClass());
  }
}
