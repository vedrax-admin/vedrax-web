package com.vedrax.math;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IMatrix {


  /**
   * get matrix entry
   *
   * @param column the column key
   * @param row    the row key
   * @return the value assigned to the coordinate
   */
  String getEntry(final String column, final String row);

  /**
   * Add an entry to the matrix
   *
   * @param column the column key
   * @param row    the row key
   * @param value  the value to be entered
   */
  void addEntry(final String column, final String row, final String value);

  /**
   * get matrix column
   *
   * @param column the column key
   * @return the column entries
   */
  Map<String, String> getColumn(String column);

  /**
   * Get column values as list of string representation
   *
   * @param column the column key
   * @return the column values
   */
  List<String> getColumnValues(String column);

  /**
   * get row keys
   *
   * @return row keys
   */
  Set<String> getRowKeys();

  /**
   * Get params
   *
   * @return params
   */
  Map<String, String> getParams();

  /**
   * Add param
   *
   * @param key   the param key to add
   * @param value the value
   */
  void addParam(String key, String value);

  /**
   * Get param by key
   *
   * @param key the param key
   * @return the value
   */
  String getParam(String key);

}
