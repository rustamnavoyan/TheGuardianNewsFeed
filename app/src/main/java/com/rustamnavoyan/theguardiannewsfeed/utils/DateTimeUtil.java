package com.rustamnavoyan.theguardiannewsfeed.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {
    private static final String DATETIME_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static Date parseDate(String dateTime) {
        SimpleDateFormat dateTimeUTCFormat = new SimpleDateFormat(DATETIME_UTC_FORMAT, Locale.US);
        dateTimeUTCFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return dateTimeUTCFormat.parse(dateTime);
        } catch (ParseException e) {
            return new Date();
        }
    }
}
