package com.github.carlhmitchell.failsafealert.utilities;

public class TimeFormatter {
    public static String formatTime(int hour, int minute) {
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
}
