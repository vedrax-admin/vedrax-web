package com.vedrax.descriptor.util;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.Optional;

public class ReflectUtil {

  /**
   * Method for getting field from data source
   *
   * @param source        the data source
   * @param attributeName the attribute to search for
   * @return the retrieved attribute information
   */
  public static Optional<Object> getField(Object source, String attributeName) {
    try {
      return Optional.of(FieldUtils.readDeclaredField(source, attributeName, true));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

}
