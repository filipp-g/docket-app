package ca.carleton.comp3004f20.androidteamalpha.app;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class Utilities {
    private final static String[] SHORT_MONTHS = new DateFormatSymbols().getShortMonths();
    private final static String[] DAY_NAMES = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    public static String formatReadableDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7)) - 1;
        int day = Integer.parseInt(date.substring(8, 10));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ", "
                + SHORT_MONTHS[month] + " " + day;
    }

}
