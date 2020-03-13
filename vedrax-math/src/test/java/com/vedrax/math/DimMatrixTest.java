package com.vedrax.math;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DimMatrixTest {

  private DimMatrix matrix;

  @Before
  public void setUp() {

    List<Matrix> matrices = new ArrayList<>();

    Matrix matrix1 = new Matrix();
    matrix1.setKey("m1");
    matrix1.addEntry(new NVP("n1", "1"));
    matrix1.addEntry(new NVP("n2", "2"));
    matrix1.addEntry(new NVP("n3", "3"));
    matrices.add(matrix1);

    Matrix matrix2 = new Matrix();
    matrix2.setKey("m2");
    matrix2.addEntry(new NVP("n1", "2"));
    matrix2.addEntry(new NVP("n2", "3"));
    matrix2.addEntry(new NVP("n3", "4"));
    matrices.add(matrix2);

    Matrix matrix3 = new Matrix();
    matrix3.setKey("m3");
    matrix3.addEntry(new NVP("n1", "2"));
    matrix3.addEntry(new NVP("n2", "2"));
    matrix3.addEntry(new NVP("n3", "1"));
    matrices.add(matrix3);

    List<NVP> params = new ArrayList<>();
    params.add(new NVP("p1", "2"));
    params.add(new NVP("p2", "4"));

    matrix = new DimMatrix(matrices, params);
  }

  @Test
  public void givenValidCodeAndIndex_whenGetEntry_thenReturnsCell() {
    String entry = matrix.getEntry("m1", "n1");
    assertThat(entry).isEqualTo("1");
  }

  @Test(expected = IllegalArgumentException.class)
  public void givenInvalidCode_whenGetEntry_thenThrowsException() {
    matrix.getEntry("invalid", "n1");
  }

  @Test
  public void givenNegativeIndex_whenGetEntry_thenReturnsNull() {
    String entry = matrix.getEntry("m1", "invalid");

    assertThat(entry).isNull();
  }

  @Test
  public void givenValidCodeAndIndex_whenAddEntry_thenOk() {
    matrix.addEntry("m2", "n4", "10");
    String value = matrix.getEntry("m2", "n4");

    assertThat(value).isEqualTo("10");
  }

  @Test(expected = NullPointerException.class)
  public void givenNoCode_whenAddEntry_thenThrowsException() {
    matrix.addEntry(null, "n1", "10");
  }

  @Test
  public void givenExistingCodeAndValue_whenAddEntry_thenOk() {
    matrix.addEntry("m1", "n1", "20");
    String entry = matrix.getEntry("m1", "n1");

    assertThat(entry).isEqualTo("20");
  }

  @Test
  public void givenValidCode_whenGetColumn_thenReturnsList() {
    Map<String, String> values = matrix.getColumn("m1");

    assertThat(values).hasSize(3);
  }

  @Test
  public void givenValidCode_whenGetColumnValues_thenReturnsSize() {
    List<String> values = matrix.getColumnValues("m1");

    assertThat(values).hasSize(3);
  }

}
