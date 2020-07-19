package com.vedrax.util;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;

public class ServletUtils {

    public static String getStringParameter(HttpServletRequest request, String name) {
        Assert.notNull(request, "request must not be null!");
        Assert.notNull(name, "name must not be null!");

        return request.getParameter(name);
    }

    public static <T extends Enum<T>> T getEnumParameter(HttpServletRequest request,
                                                         Class<T> enumClass,
                                                         String name) {
        String value = getStringParameter(request, name);
        return EnumUtils.getEnum(enumClass, value);
    }

    public static Long getLongParameter(HttpServletRequest request, String name) {
        String param = getStringParameter(request, name);
        return NumberUtils.createLong(param);
    }

    public static Integer getIntParameter(HttpServletRequest request, String name) {
        String param = getStringParameter(request, name);
        return NumberUtils.createInteger(param);
    }

    public static Date getDateParameter(HttpServletRequest request, String name) {
        String param = getStringParameter(request, name);
        LocalDate localDate = DateUtils.convertToLocalDate(param);
        return DateUtils.convertToDate(localDate);
    }

    public static byte[] getBytesParameter(HttpServletRequest request, String name) {
        String param = getStringParameter(request, name);

        if (param == null) {
            throw new IllegalArgumentException("Content for parameter [" + name + "] unavailable!");
        }

        return Base64.getDecoder().decode(param);
    }
}
