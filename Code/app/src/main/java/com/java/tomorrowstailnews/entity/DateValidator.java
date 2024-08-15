package com.java.tomorrowstailnews.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class DateValidator {
    public boolean isValidDate(String date) {
        if (!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date)) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        try {
            format.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidDateTime(String dateTime) {
        if (!Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", dateTime)) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setLenient(false);
        try {
            format.parse(dateTime);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValid(String date) {
        return isValidDate(date) || isValidDateTime(date);
    }

    public long toSecs(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(isValidDateTime(dateString) ? dateString : dateString + " 00:00:00");
            return date.getTime();
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean isBefore(String date1, String date2) {
        return toSecs(date1) < toSecs(date2);
    }
}