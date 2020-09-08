package com.vedrax.descriptor.typeof;

import java.util.function.Consumer;

public class Then<S> {

  private final S object;

  Then(S object) {
    this.object = object;
  }

  public <T> ThenIs<S, T> is(Class<T> type) {
    return new ThenIs<>(this, object, type);
  }

  public void orElse(Consumer<S> orElseBlock) {
    orElseBlock.accept(object);
  }
}
