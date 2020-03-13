package com.vedrax.math;

import org.junit.Test;

import java.io.UncheckedIOException;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilTest {

  @Test
  public void givenFileName_whenReadResourceToString_thenGetResourceAsString() {
    String resource = Util.readResourceToString("math.min.js");
    assertThat(resource).isNotNull();
  }

  @Test(expected = UncheckedIOException.class)
  public void givenInvalidFileName_whenReadResourceToString_thenGetException() {
    String resource = Util.readResourceToString("invalid.js");
    assertThat(resource).isNotNull();
  }

  @Test(expected = NullPointerException.class)
  public void givenNoFileName_whenReadResourceToString_thenGetException() {
    Util.readResourceToString(null);
  }

  @Test
  public void givenObjectAsArray_whenIsArray_thenTrue() {
    String[] obj = {"a", "b", "c"};

    assertThat(Util.isArray(obj)).isTrue();
  }

  @Test
  public void givenObject_whenIsArray_thenFalse() {
    String obj = "a";

    assertThat(Util.isArray(obj)).isFalse();
  }

  @Test(expected = NullPointerException.class)
  public void givenNoObject_whenIsArray_thenGetException() {
    Util.isArray(null);
  }

  @Test
  public void givenArrayOfObject_whenConvertObjectToArray_thenReturnsArrayOfString() {
    Object[] objects = {"a", "b", "c"};
    String[] list = Util.convertObjectToArray(objects);

    assertThat(list).hasSize(3);
  }

  @Test(expected = NullPointerException.class)
  public void givenNoArrayOfObject_whenConvertObjectToArray_thenGetException() {
    Util.convertObjectToArray(null);
  }

  @Test
  public void givenArrayOfObject_whenConvertObjectToString_thenReturnsString() {
    Object[] objects = {"a", "b", "c"};
    String representation = Util.convertObjectToString(objects);

    assertThat(representation).isEqualTo("[a,b,c]");
  }

  @Test(expected = NullPointerException.class)
  public void givenNoArrayOfObject_whenConvertObjectToString_thenGetException() {
    Util.convertObjectToString(null);
  }
}
