package com.github.carlhmitchell.failsafealert.utilities;

//??

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
}
