package com.github.carlhmitchell.failsafealert.utilities;

//Model

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.carlhmitchell.failsafealert.BackgroundService;
import com.github.carlhmitchell.failsafealert.utilities.background.AlarmReceiver;

import java.sql.Time;
import java.util.Calendar;
import java.util.Objects;

import static android.app.PendingIntent.getBroadcast;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.ACTION_ALERT;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.ACTION_NOTIFICATION;


public class ScheduleAlarms {
    private static final String DEBUG_TAG = ScheduleAlarms.class.getSimpleName();
// --Commented out by Inspection START (2018-07-18 17:17):
//    public ScheduleAlarms() {
//    }
// --Commented out by Inspection STOP (2018-07-18 17:17)

// --Commented out by Inspection START (2018-07-18 17:17):
//    public ScheduleAlarms(Context context) {
//        run(context);
//    }
// --Commented out by Inspection STOP (2018-07-18 17:17)

    // Set up a recurring alarm ever day (or sooner for testing, 1 minute here)
    public static void run(Context context) {
        ContextWrapper wrapper = new ContextWrapper(context);
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());

        // Construct an Intent that will execute the AlarmReceiver
        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.setAction(ACTION_NOTIFICATION);
        // Create a PendingIntent to be triggered when the alarm goes off
        // The PendingIntent.FLAG_UPDATE_CURRENT means that if the alarm fires quickly the events replace each other rather than stack up.
        PendingIntent notificationPendingIntent = getBroadcast(context, AlarmReceiver.NOTIFICATION, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent alertIntent = new Intent(context, AlarmReceiver.class);
        alertIntent.setAction(ACTION_ALERT);
        // Create a PendingIntent to be triggered when the alarm goes off
        // The PendingIntent.FLAG_UPDATE_CURRENT means that if the alarm fires quickly the events replace each other rather than stack up.
        PendingIntent alertPendingIntent = getBroadcast(context, AlarmReceiver.ALERT, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String notificationTime = data.getString("pref_notification_time", "20:00");
        String alertTime = data.getString("pref_alert_time", "22:00");

        int notificationHour = TimeUtilities.getHour(notificationTime);
        int notificationMinute = TimeUtilities.getMinute(notificationTime);
        int alertHour = TimeUtilities.getHour(alertTime);
        int alertMinute = TimeUtilities.getMinute(alertTime);

        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        int currentMinutes = currentHour * 60 + currentMinute;
        int notificationMinutes = notificationHour * 60 + notificationMinute;
        int alertMinutes = alertHour * 60 + alertMinute;

        Calendar notificationCalendar = Calendar.getInstance();
        notificationCalendar.set(Calendar.HOUR_OF_DAY, notificationHour);
        notificationCalendar.set(Calendar.MINUTE, notificationMinute);
        notificationCalendar.set(Calendar.SECOND, 0);
        notificationCalendar.set(Calendar.MILLISECOND, 0);
        String dbg_notificationString = TimeUtilities.formatCalendar(notificationCalendar);

        Calendar alertCalendar = Calendar.getInstance();
        alertCalendar.set(Calendar.HOUR_OF_DAY, alertHour);
        alertCalendar.set(Calendar.MINUTE, alertMinute);
        alertCalendar.set(Calendar.SECOND, 0);
        alertCalendar.set(Calendar.MILLISECOND, 0);
        String dbg_alertString = TimeUtilities.formatCalendar(alertCalendar);
        SDLog.d(DEBUG_TAG, "Notification time: " + dbg_notificationString +
                           "\nAlert time:" + dbg_alertString);
        if (notificationMinutes < currentMinutes) {
            //Turn on the alarm.
            SDLog.w(DEBUG_TAG, "Warning, notification set for the past. Turning notification on.");
            Intent pastNotificationIntent = new Intent(context, BackgroundService.class);
            pastNotificationIntent.setAction(ACTION_NOTIFICATION);
            context.startService(pastNotificationIntent);
        }

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        Objects.requireNonNull(alarm).setRepeating(AlarmManager.RTC_WAKEUP, notificationCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, notificationPendingIntent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, alertCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alertPendingIntent);
        SDLog.i(DEBUG_TAG, "run got called: " + notificationTime + " - " + alertTime);
    }
}
