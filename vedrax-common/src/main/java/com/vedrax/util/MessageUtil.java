package com.vedrax.util;

import org.apache.commons.lang3.Validate;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class MessageUtil {

  /**
   * Method for getting the message
   *
   * @param key    the message key
   * @param params the parameters if any
   * @return the message
   */
  public static String getMessageFromKey(MessageSource messageSource,
                                         String key,
                                         Object[] params,
                                         Locale locale) {
    Validate.notNull(messageSource, "message source must be provided");
    Validate.notNull(key, "key must be provided");

    try {
      return messageSource.getMessage(key, params, locale);
    } catch (Exception ex) {
      return null;
    }
  }
}
