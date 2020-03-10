package com.vedrax.descriptor.util;

import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

  /**
   * Convert a {@link Date} to a {@link LocalDate}
   *
   * @param dateToConvert - The date to convert
   * @return LocalDate
   */
  public static LocalDate convertToLocalDate(Date dateToConvert) {
    Validate.notNull(dateToConvert, "Null dateToConvert not allowed");

    return dateToConvert.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDate();
  }

  /**
   * Method for converting a date to an ISO string
   *
   * @param date - The date to convert
   * @return String
   */
  public static String dateToISO(Date date) {
    if (date == null) {
      return null;
    }
    LocalDate localDate = convertToLocalDate(date);
    return localDate.toString();
  }

}
