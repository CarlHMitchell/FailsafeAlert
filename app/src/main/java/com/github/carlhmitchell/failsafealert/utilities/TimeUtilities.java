package com.github.carlhmitchell.failsafealert.utilities;

//??

import java.util.Calendar;

public class TimeUtilities {
    /**
     * Extracts the hour as an integer from a string formatted as "HH:MM"
     * @param time is a string in the format "HH:MM"
     * @return an integer, equal to HH
     */
    public static int getHour(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[0]));
    }

    /**
     * Extracts the minute as an integer from a string formatted as "HH:MM"
     * @param time is a string in the format "HH:MM"
     * @return an integer, equal to MM
     */
    public static int getMinute(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[1]));
    }

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


    public static String getFormattedTime() {
        Calendar currentTime = Calendar.getInstance();
        int year = currentTime.get(Calendar.YEAR);
        int month = currentTime.get(Calendar.MONTH);
        int day = currentTime.get(Calendar.DAY_OF_MONTH);
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        int second = currentTime.get(Calendar.SECOND);
        String monthString = normalizeTwoDigitInt(month);
        String dayString = normalizeTwoDigitInt(day);
        String hourString = normalizeTwoDigitInt(hour);
        String minuteString = normalizeTwoDigitInt(minute);
        String secondString = normalizeTwoDigitInt(second);
        return String.valueOf(year) + "-" + monthString + "-" + dayString + " at "+
               hourString + ":" + minuteString + ":" + secondString;
    }

    public static String formatCalendar(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String hourString = normalizeTwoDigitInt(hour);
        String minuteString = normalizeTwoDigitInt(minute);
        String secondString = normalizeTwoDigitInt(second);
        return hourString + ":" + minuteString + ":" + secondString;
    }

    /**
     * Take an input int for a day/hour/minute/second, and output a String padded with a
     * leading zero if needed.
     * @param time input integer to be padded if necessary
     * @return String padded to two digits with a leading zero
     */
    private static String normalizeTwoDigitInt(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return String.valueOf(time);
        }
    }
}
