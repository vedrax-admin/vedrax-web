package com.vedrax.descriptor.typeof;

import java.util.function.Function;

public class ReturnIs<S, T, R> {

  final S object;
  private final Class<T> expectedType;

  public ReturnIs(S object, Class<T> expectedType) {
    this.object = object;
    this.expectedType = expectedType;
  }

  public ThenReturn<S, R> thenReturn(Function<T, R> resultFun) {
    if (object != null && expectedType.isAssignableFrom(object.getClass())) {
      final R result = resultFun.apply((T) object);
      return new TerminalThenReturn<>(object, result);
    }
    return new ThenReturn<>(object);
  }

  public ThenReturn<S, R> thenReturn(R result) {
    if (object != null && expectedType.isAssignableFrom(object.getClass())) {
      return new TerminalThenReturn<>(object, result);
    }
    return new ThenReturn<>(object);
  }
}
