package com.vedrax.util;

import org.apache.commons.lang3.Validate;
import org.springframework.util.NumberUtils;

import java.math.BigDecimal;

public class NumUtils {

  public boolean assertIsNumber(String value) {
    return org.apache.commons.lang3.math.NumberUtils.isParsable(value);
  }

  public static BigDecimal toBigDecimal(String value) {

    return NumberUtils.parseNumber(value, BigDecimal.class);
  }

  public static BigDecimal toBigDecimal(String value, int scale) {
    Validate.isTrue(scale >= 0, "scale must be greater than 0");
    BigDecimal number = toBigDecimal(value);
    return number.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
  }

  public static BigDecimal toPositiveBigDecimal(String value, String error) {
    BigDecimal number = toBigDecimal(value);
    assertGreaterThan(number, "0.0", error);
    return number.setScale(2, BigDecimal.ROUND_HALF_EVEN);
  }

  public static void assertGreaterThan(BigDecimal number, String controlNumber,
                                       String message) {
    Validate.notNull(number, "number must be provided");
    Validate.notNull(controlNumber, "control number must be provided");
    Validate.notNull(message, "message must be provided");

    if (number.compareTo(toBigDecimal(controlNumber)) < 0) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void ValidateLessThan(BigDecimal number, String controlNumber,
                                      String message) {
    Validate.notNull(number, "number must be provided");
    Validate.notNull(controlNumber, "control number must be provided");
    Validate.notNull(message, "message must be provided");

    if (number.compareTo(toBigDecimal(controlNumber)) > 0) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void ValidateEqual(BigDecimal number, String controlNumber,
                                   String message) {
    Validate.notNull(number, "number must be provided");
    Validate.notNull(controlNumber, "control number must be provided");
    Validate.notNull(message, "message must be provided");

    if (number.compareTo(toBigDecimal(controlNumber)) != 0) {
      throw new IllegalArgumentException(message);
    }
  }

  public static BigDecimal addPercentageToNumber(BigDecimal number, BigDecimal percent) {
    assertGreaterThan(number, "0.0", "number must be greater than 0.0");
    assertGreaterThan(percent, "0.0", "percent must be greater than 0.0");
    ValidateLessThan(percent, "1.0", "percent must be less than 1.0");

    return number.multiply(BigDecimal.ONE.add(percent)).setScale(2, BigDecimal.ROUND_HALF_EVEN);

  }

  public static BigDecimal multiply(BigDecimal operandA, BigDecimal operandB) {
    Validate.notNull(operandA, "operand A must be provided");
    Validate.notNull(operandB, "operand B must be provided");

    return operandA.multiply(operandB).setScale(2, BigDecimal.ROUND_HALF_EVEN);
  }

  public static BigDecimal multiplyWithInteger(BigDecimal operandA, Integer operandB) {
    Validate.notNull(operandA, "operand A must be provided");
    Validate.notNull(operandB, "operand B must be provided");

    BigDecimal operandBToBigDecimal = new BigDecimal(operandB);

    return multiply(operandA, operandBToBigDecimal);
  }

  public static Long valueOf(String value) {
    return NumberUtils.parseNumber(value, Long.class);
  }

}
