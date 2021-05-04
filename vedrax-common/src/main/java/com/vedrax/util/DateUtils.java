package com.vedrax.util;

import org.apache.commons.lang3.Validate;
import org.springframework.util.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

/**
 * @author remypenchenat
 */
public class DateUtils {

    private static final String YYYY_MM = "yyyyMM";

    /**
     * Convert a {@link Date} to a {@link LocalDate}
     *
     * @param dateToConvert - The date to convert
     * @return LocalDate
     */
    public static LocalDate convertToLocalDate(Date dateToConvert) {
        Validate.notNull(dateToConvert, "Null dateToConvert not allowed");

        //avoid UnsupportedOperationException for java.sql.Date
        Date safeDate = new Date(dateToConvert.getTime());

        return safeDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Method for returning the number of days between 2 dates
     *
     * @param start the starting date
     * @param end   the ending date
     * @return number of days
     */
    public static long daysBetween(Date start, Date end) {
        Validate.notNull(start, "Null start not allowed");
        Validate.notNull(end, "Null end not allowed");

        LocalDate startLD = convertToLocalDate(start);
        LocalDate endLD = convertToLocalDate(end);

        if (startLD.isAfter(endLD)) {
            throw new IllegalArgumentException("start date must be before end date.");
        }

        return startLD.until(endLD, DAYS);
    }

    /**
     * Count business days between 2 days
     *
     * @param start start date
     * @param end   end date
     * @return number of business days between 2 dates
     */
    public static long countBusinessDaysBetween(Date start, Date end) {
        Validate.notNull(start, "Null start not allowed");
        Validate.notNull(end, "Null end not allowed");

        LocalDate startDate = convertToLocalDate(start);
        LocalDate endDate = convertToLocalDate(end);

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        return Stream.iterate(startDate, date -> date.plusDays(1)).limit(daysBetween)
                .filter(isWeekend.negate()).count();
    }

    /**
     * Convert a {@link LocalDate} to a {@link Date}
     *
     * @param dateToConvert - The LocalDate to convert
     * @return Date
     */
    public static Date convertToDate(LocalDate dateToConvert) {
        Validate.notNull(dateToConvert, "Null dateToConvert not allowed");

        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    /**
     * Convert a compliant ISO string to a {@link LocalDate} otherwise returns
     * null if not compliant
     *
     * @param dateToConvert - The ISO string
     * @return LocalDate or null if the string is not compliant
     */
    public static LocalDate convertToLocalDate(String dateToConvert) {
        Validate.notNull(dateToConvert, "Null dateToConvert not allowed");

        LocalDate localDate;

        try {
            localDate = LocalDate.parse(dateToConvert);
        } catch (DateTimeParseException ex) {
            localDate = null;
        }

        return localDate;
    }

    /**
     * Convert a compliant ISO string to a {@link LocalDate} otherwise throws an
     * exception
     *
     * @param dateToConvert
     * @return
     * @throws IllegalArgumentException
     */
    public static LocalDate safeConvertToLocalDate(String dateToConvert) throws IllegalArgumentException {
        LocalDate localDate = convertToLocalDate(dateToConvert);

        if (localDate == null) {
            throw new IllegalArgumentException("Text [" + dateToConvert + "] is not a compliant ISO date.");
        }

        return localDate;
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

    /**
     * Convert a {@link Date} to a {@link LocalDateTime}
     *
     * @param dateToConvert - The date to convert
     * @return LocalDateTime
     */
    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        Validate.notNull(dateToConvert, "Null dateToConvert not allowed");

        //avoid UnsupportedOperationException for java.sql.Date
        Date safeDate = new Date(dateToConvert.getTime());

        return safeDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

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
     * Convert a compliant ISO string to a {@link LocalDateTime} otherwise
     * returns null if not compliant
     *
     * @param dateToConvert - The ISO string
     * @return LocalDateTime or null if the string is not compliant
     */
    public static LocalDateTime convertToLocalDateTime(String dateToConvert) {
        Validate.notNull(dateToConvert, "Null dateToConvert not allowed");

        LocalDateTime localDateTime;

        try {
            localDateTime = LocalDateTime.parse(dateToConvert);
        } catch (DateTimeParseException ex) {
            localDateTime = null;
        }

        return localDateTime;
    }

    /**
     * Convert a compliant ISO string to a {@link LocalDate} otherwise throws an
     * exception
     *
     * @param dateToConvert - The ISO string
     * @return LocalDateTime
     * @throws IllegalArgumentException
     */
    public static LocalDateTime safeConvertToLocalDateTime(String dateToConvert) throws IllegalArgumentException {
        LocalDateTime localDateTime = convertToLocalDateTime(dateToConvert);

        if (localDateTime == null) {
            throw new IllegalArgumentException("Text [" + dateToConvert + "] is not a compliant ISO date.");
        }

        return localDateTime;
    }

    /**
     * Method for converting a datetime to an ISO string
     *
     * @param date - The date to convert
     * @return String
     */
    public static String datetimeToISO(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDate = convertToLocalDateTime(date);
        return localDate.toString();
    }

    /**
     * Method for returning the current date in the following format yyyyMM
     *
     * @return String
     */
    public static String getCurrentYYYYMM() {
        //now
        LocalDate currentDate = LocalDate.now();
        //get formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM);

        //apply formatter
        return currentDate.format(formatter);
    }

    /**
     * Get the formatting ISO date to the following format yyyyMM.
     *
     * @param dateStr - The string to parse
     * @return String
     */
    public static String getYYYYMM(String dateStr) {

        //get LocalDateTime by parsing the dateStr
        LocalDateTime currentDate = safeConvertToLocalDateTime(dateStr);

        //get formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM);

        //apply formatter
        return currentDate.format(formatter);
    }

    public static String printDateWithFormat(Date date, String format) {
        if (date == null) {
            return "";
        }

        //get formatter
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);

    }

    /**
     * Method for checking if a providing date is before now.
     *
     * @param date - The date to be checked
     * @return boolean
     */
    public static boolean dateBeforeNow(Date date) {
        if (date == null) {
            return false;
        }

        //convert date to LocalDate
        LocalDate testingLocalDate = convertToLocalDate(date);

        //now
        LocalDate currentLocalDate = LocalDate.now();

        //perform check
        return testingLocalDate.isBefore(currentLocalDate);
    }

    /**
     * Get days between now
     *
     * @param date
     * @return
     */
    public static long dayBetweenNow(Date date) {
        if (date == null) {
            return -1;
        }

        //convert date to LocalDate
        LocalDate beforeLocalDate = convertToLocalDate(date);

        //now
        LocalDate currentLocalDate = LocalDate.now();

        return DAYS.between(beforeLocalDate, currentLocalDate);

    }

    /**
     * Method for checking if a providing date is after now.
     *
     * @param date - The date to be checked
     * @return boolean
     */
    public static boolean dateAfterNow(Date date) {
        if (date == null) {
            return false;
        }

        //convert date to LocalDate
        LocalDate testingLocalDate = convertToLocalDate(date);

        //now
        LocalDate currentLocalDate = LocalDate.now();

        //perform check
        return testingLocalDate.isAfter(currentLocalDate);
    }

    /**
     * Get current week number
     *
     * @return
     */
    public static int getWeekOfYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Add days to current date
     *
     * @param n - The number of days to add
     * @return Date
     */
    public static Date addDaysToCurrent(long n) {

        // convert date to localdate
        LocalDate localDate = LocalDate.now();

        //add days to localdate
        localDate = safeAddUnitToLocalDate(localDate, n, ChronoUnit.DAYS);

        // convert LocalDateTime to date
        return convertToDate(localDate);

    }

    /**
     * Add days to a specific date
     *
     * @param date - The date to add
     * @param n    - The number of days to add
     * @return Date
     */
    public static Date addDaysToDate(Date date, long n) {
        Validate.notNull(date, "Null date not allowed");

        LocalDate localDate = convertToLocalDate(date);
        localDate = safeAddUnitToLocalDate(localDate, n, DAYS);
        return convertToDate(localDate);
    }

    public static Date addMonthsToDate(Date date, long n) {
        Validate.notNull(date, "Null date not allowed");

        LocalDate localDate = convertToLocalDate(date);
        localDate = safeAddUnitToLocalDate(localDate, n, MONTHS);
        return convertToDate(localDate);
    }

    /**
     * Add Unit to Local Date
     *
     * @param localDate - The localDate to add unit
     * @param n         - The unit number
     * @param unit      - Days, Month, Year
     * @return LocalDate
     */
    public static LocalDate addUnitToLocalDate(LocalDate localDate, long n, TemporalUnit unit) {
        Validate.notNull(localDate, "Null localDate not allowed");
        Validate.isTrue(n > 0, "n must be greater to 0");

        LocalDate localDatePlusUnits;

        try {

            localDatePlusUnits = localDate.plus(n, unit);

        } catch (DateTimeException | ArithmeticException ex) {
            localDatePlusUnits = null;
        }

        return localDatePlusUnits;
    }

    public static LocalDate safeAddUnitToLocalDate(LocalDate localDate, long n, TemporalUnit unit) throws IllegalArgumentException {
        LocalDate localDatePlusUnits = addUnitToLocalDate(localDate, n, unit);

        if (localDatePlusUnits == null) {
            throw new IllegalArgumentException("Cannot add " + n + " " + unit + " to [" + localDate + "].");
        }

        return localDatePlusUnits;
    }

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

    public static LocalDate getFirstDayOfNextMonth() {
        LocalDate now = LocalDate.now();
        return now.with(TemporalAdjusters.firstDayOfNextMonth());
    }

    public static LocalDate getLastDayOfMonth(LocalDate localDate) {
        Assert.notNull(localDate, "localDate must not be null");

        return localDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static long getCurrentTimeAsLong() {
        return new Date().getTime();
    }

}
