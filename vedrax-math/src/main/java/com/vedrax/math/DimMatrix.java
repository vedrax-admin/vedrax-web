package com.vedrax.math;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DimMatrix implements IMatrix {

  private Map<String, Map<String, String>> data = new HashMap<>();
  private Map<String, String> params = new HashMap<>();
  private Set<String> rowKeys = new HashSet<>();

  public DimMatrix(List<Matrix> data, List<NVP> params) {
    Objects.requireNonNull(data, "data must be provided");
    Objects.requireNonNull(params, "params must be provided");

    dataToMap(data);
    nvpToMap(params);
  }

  private void dataToMap(List<Matrix> matrixList) {
    matrixList.forEach(matrix -> {
      //1. get all entries in the matrix
      List<NVP> entries = matrix.getEntries();
      //2. get the row keys for the first entry
      initRowKeys(entries);
      //3.convert entries to map structure
      Map<String, String> entriesMap = new HashMap<>();
      entries.forEach(entry -> {
        //4. check number integrity
        checkEntry(entry.getValue());
        //5. put NVP to map
        entriesMap.put(entry.getKey(), entry.getValue());
      });
      //6. update data map
      data.put(matrix.getKey(), entriesMap);
    });
  }

  private void nvpToMap(List<NVP> nvpList) {
    nvpList.forEach(param -> {
      String value = param.getValue();
      checkEntry(value);
      params.put(param.getKey(), value);
    });
  }

  private void checkEntry(String value) {
    boolean isNumber = NumberUtils.isCreatable(value);

    if (!isNumber) {
      throw new IllegalArgumentException("The value [" + value + "] is not a valid number");
    }
  }

  private void initRowKeys(List<NVP> nvpList) {
    if (rowKeys.isEmpty()) {
      rowKeys = nvpList.stream().map(NVP::getKey).collect(Collectors.toSet());
    }
  }

  @Override
  public String getEntry(final String column, final String row) {
    Map<String, String> values = getColumn(column);
    return values.get(row);
  }

  @Override
  public void addEntry(final String column, final String row, final String value) {
    Validate.notNull(column, "column must be provided");
    Validate.notNull(row, "row must be provided");
    Validate.notNull(value, "value must be provided");
    checkIfNumber(column, row, value);

    Map<String, String> values = data.computeIfAbsent(column, k -> new HashMap<>());

    values.put(row, value);
  }

  private void checkIfNumber(String column, String row, String value) {
    Validate.isTrue(NumberUtils.isCreatable(value), "The value " + value + " for the matrix [" + column + "] [" + row + "] is not a number");
  }

  @Override
  public Map<String, String> getColumn(String code) {
    Validate.notNull(code, "code must be provided");

    Map<String, String> values = data.get(code);

    if (values == null) {
      throw new IllegalArgumentException("No data for the specified key: " + code);
    }

    return values;
  }

  @Override
  public List<String> getColumnValues(String column) {
    return new ArrayList<>(getColumn(column).values());
  }

  @Override
  public Set<String> getRowKeys() {
    return rowKeys;
  }

  @Override
  public Map<String, String> getParams() {
    return params;
  }

  @Override
  public void addParam(String key, String value) {
    params.put(key, value);
  }

  @Override
  public String getParam(String key) {
    Validate.notNull(key, "key must be provided");

    String value = params.get(key);

    Validate.notNull(value, "The param with key [" + key + "] does not exist.");

    return value;
  }

}
