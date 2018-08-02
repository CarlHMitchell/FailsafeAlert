package com.github.carlhmitchell.failsafealert.utilities;

import java.util.Calendar;

public class TimeFormatter {

    public static String formatTimeFromIntegers(int hour, int minute) {
        String hourString;
        String minuteString;
        if (hour < 10) {
            hourString = "0" + String.valueOf(hour);
        } else {
            hourString = String.valueOf(hour);
        }

        if (minute < 10) {
            minuteString = "0" + String.valueOf(minute);
        } else {
            minuteString = String.valueOf(minute);
        }
        return hourString + ":" + minuteString;
    }


    public String getFormattedTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String monthString = normalizeTwoDigitInt(month);
        String dayString = normalizeTwoDigitInt(day);
        String hourString = normalizeTwoDigitInt(hour);
        String minuteString = normalizeTwoDigitInt(minute);
        String secondString = normalizeTwoDigitInt(second);
        return String.valueOf(year) + "-" + monthString + "-" + dayString + "T"+
               hourString + ":" + minuteString + ":" + secondString;
    }

    /**
     * Take an input int for a day/hour/minute/second, and output a String padded with a
     * leading zero if needed.
     * @param time
     * @return
     */
    private String normalizeTwoDigitInt(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return String.valueOf(time);
        }
    }
}
