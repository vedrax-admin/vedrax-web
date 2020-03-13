package com.vedrax.math;

import org.apache.commons.lang3.Validate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Util {

  private Util(){}

  /**
   * Read resource file
   *
   * @param name the resource file name
   * @return the resource as a string
   */
  public static String readResourceToString(String name) {
    Validate.notNull(name, "name must be provided");

    Resource resource = new ClassPathResource(name);
    return asString(resource);
  }

  /**
   * method for getting the resource as a string
   *
   * @param resource the resource
   * @return string
   */
  private static String asString(Resource resource) {
    try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
      return FileCopyUtils.copyToString(reader);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  /**
   * Helper method for checking if an object is of type array
   *
   * @param obj the returned value of the scope
   * @return true if array otherwise false
   */
  public static boolean isArray(Object obj) {
    Validate.notNull(obj,"obj must be provided");

    return obj.getClass().isArray();
  }

  /**
   * Method for converting an array of object to an array of string
   *
   * @param items the array of object to be converted
   * @return array of string
   */
  public static String[] convertObjectToArray(Object[] items) {
    Validate.notNull(items,"items must be provided");

    return Arrays.copyOf(items, items.length, String[].class);
  }

  /**
   * format object to string or to array of string
   *
   * @param val the object returned
   * @return the string
   */
  public static String convertObjectToString(Object val) {
    if (isArray(val)) {
      Object[] objects = (Object[]) val;
      String[] values = convertObjectToArray(objects);
      return "[" + String.join(",", values) + "]";
    }

    return String.valueOf(val);
  }
}
