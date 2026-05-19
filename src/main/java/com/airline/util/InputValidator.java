package com.airline.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Utility class for validating user input in the console application.
 */
public class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,15}$");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        DATE_FORMAT.setLenient(false);
    }

    /**
     * Validates if the email format is correct.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates if the phone number is correct (10 to 15 digits).
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validates if a string is a valid date-time in the format yyyy-MM-dd HH:mm:ss.
     */
    public static boolean isValidDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return false;
        try {
            DATE_FORMAT.parse(dateTimeStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Converts a valid date string to Timestamp.
     */
    public static Timestamp toTimestamp(String dateTimeStr) {
        try {
            Date parsedDate = DATE_FORMAT.parse(dateTimeStr);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Formats a Timestamp to String.
     */
    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "";
        return DATE_FORMAT.format(timestamp);
    }
}
