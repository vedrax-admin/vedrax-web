package com.vedrax.security.util;

import org.apache.commons.lang3.Validate;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Date;

public class DateUtil {

  /**
   * Convert a {@link LocalDateTime} to a {@link Date}
   *
   * @param dateToConvert - The LocalDateTime to convert
   * @return Date
   */
  public static Date convertToDateTime(LocalDateTime dateToConvert) {
    Validate.notNull(dateToConvert, "Null dateToConvert not allowed");

    return Date.from(dateToConvert.atZone(ZoneId.systemDefault())
      .toInstant());
  }

  /**
   * Convert a {@link Date} to a {@link LocalDateTime}
   *
   * @param dateToConvert - The date to convert
   * @return LocalDateTime
   */
  public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
    Validate.notNull(dateToConvert, "Null dateToConvert not allowed");

    return dateToConvert.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDateTime();
  }

  /**
   * Add Unit to Local Date
   *
   * @param localDateTime - The localDate TIME to add unit
   * @param n - The unit number
   * @param unit - Days, Month, Year
   * @return LocalDate
   */
  public static LocalDateTime addUnitToLocalDateTime(LocalDateTime localDateTime, long n, TemporalUnit unit) {
    Validate.notNull(localDateTime, "Null localDateTime not allowed");
    Validate.isTrue(n > 0, "n must be greater to 0");

    LocalDateTime localDatePlusUnits;

    try {

      localDatePlusUnits = localDateTime.plus(n, unit);

    } catch (DateTimeException | ArithmeticException ex) {
      localDatePlusUnits = null;
    }

    return localDatePlusUnits;
  }



}
