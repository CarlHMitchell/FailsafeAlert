package com.github.carlhmitchell.failsafealert.utilities;

//N/A

@SuppressWarnings("WeakerAccess") // These constants should be available app-wide.
public class AppConstants {
    public static final int SWITCH_INACTIVE = 0;
    public static final int SWITCH_ACTIVE = 1;
    public static final String NOTIFICATION_CHANNEL_ID = "Debug Channel";
    public static final long MINUTE_MILLIS = 1000 * 60;
    public static final long WAKELOCK_TIMEOUT = MINUTE_MILLIS * 10;
    public static final boolean LOG_TO_SD = true;
    public static final String ACTION_NOTIFICATION = "com.github.carlhmitchell.failsafealert.NOTIFICATION";
    public static final String ACTION_ALERT = "com.github.carlhmitchell.failsafealert.ALERT";
    public static final String ACTION_STARTUP = "com.github.carlhmitchell.failsafealert.STARTUP";
    public static final String ACTION_CANCEL_NOTIFICATION = "com.github.carlhmitchell.failsafealert.CANCEL_NOTIFICATION";
}
