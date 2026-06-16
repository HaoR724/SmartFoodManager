package com.example.smartfood.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static final String PATTERN = "yyyy-MM-dd";

    public static String getToday() {
        return format(new Date());
    }

    public static String addDays(String date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse(date));
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return format(calendar.getTime());
    }

    public static long daysBetween(String startDate, String endDate) {
        long diff = parse(endDate).getTime() - parse(startDate).getTime();
        return diff / (24L * 60L * 60L * 1000L);
    }

    public static Date parse(String date) {
        try {
            return new SimpleDateFormat(PATTERN, Locale.CHINA).parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String format(Date date) {
        return new SimpleDateFormat(PATTERN, Locale.CHINA).format(date);
    }
}
