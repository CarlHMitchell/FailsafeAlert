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

import java.util.Calendar;
import java.util.Objects;

import static android.app.PendingIntent.getBroadcast;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.ACTION_ALERT;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.ACTION_NOTIFICATION;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.SWITCH_ACTIVE;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.SWITCH_INACTIVE;


public class ScheduleAlarms {
    private static final String DEBUG_TAG = ScheduleAlarms.class.getSimpleName();

    private static Calendar setupCalendarFromPref(Context context, String preference, String def) {
        ContextWrapper wrapper = new ContextWrapper(context);
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());

        String timeString = data.getString(preference, def);
        int hour = TimeUtilities.getHour(timeString);
        int minute = TimeUtilities.getMinute(timeString);

        Calendar output = Calendar.getInstance();
        output.set(Calendar.HOUR_OF_DAY, hour);
        output.set(Calendar.MINUTE, minute);
        output.set(Calendar.SECOND, 0);
        output.set(Calendar.MILLISECOND, 0);

        String dbg_outputCalendarString = TimeUtilities.formatCalendar(output);
        SDLog.d(DEBUG_TAG, "Calendar setup:\n" + dbg_outputCalendarString);

        return output;
    }

    private static void scheduleAlarm(ContextWrapper wrapper, Intent intent, Intent pastIntent, String prefToSelect, String prefDefaultValue, String action, boolean addDay) {
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());

        intent.setAction(action);

        Calendar alarmCalendar = setupCalendarFromPref(wrapper, prefToSelect, prefDefaultValue);

        if (addDay) {
            alarmCalendar.add(Calendar.HOUR_OF_DAY, 24);
        }

        if (action.equals(ACTION_NOTIFICATION)) {
            int alarmMinutes = alarmCalendar.get(Calendar.HOUR_OF_DAY) * 60 + alarmCalendar.get(Calendar.MINUTE);
            Calendar currentTime = Calendar.getInstance();
            int currentMinutes = currentTime.get(Calendar.HOUR_OF_DAY) * 60 + currentTime.get(Calendar.MINUTE);
            if (alarmMinutes < currentMinutes) {
                if (data.getInt("state", SWITCH_INACTIVE) == SWITCH_ACTIVE) {
                    SDLog.w(DEBUG_TAG, "Notification set for the past. Turning notification on.");
                    pastIntent.setAction(ACTION_NOTIFICATION);
                    wrapper.startService(pastIntent);
                }
            }
        }

        AlarmManager alarm = (AlarmManager) wrapper.getSystemService(Context.ALARM_SERVICE);
        if (action.equals(ACTION_NOTIFICATION)) {
            // Create a PendingIntent to be triggered when the alarm goes off
            // The PendingIntent.FLAG_UPDATE_CURRENT means that if the alarm fires quickly the events replace each other rather than stack up.
            PendingIntent pendingIntent = getBroadcast(wrapper, AlarmReceiver.NOTIFICATION, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Objects.requireNonNull(alarm).setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            // Create a PendingIntent to be triggered when the alarm goes off
            // The PendingIntent.FLAG_UPDATE_CURRENT means that if the alarm fires quickly the events replace each other rather than stack up.
            PendingIntent pendingIntent = getBroadcast(wrapper, AlarmReceiver.ALERT, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Objects.requireNonNull(alarm).setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private static void scheduleNotification(Context context, boolean addDay) {
        ContextWrapper wrapper = new ContextWrapper(context);
        // Construct an Intent that will execute the AlarmReceiver
        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        Intent pastNotificationIntent = new Intent(context, BackgroundService.class);

        scheduleAlarm(wrapper, notificationIntent, pastNotificationIntent, "pref_notification_time", "21:00", ACTION_NOTIFICATION, addDay);
    }

    private static void scheduleAlert(Context context) {
        ContextWrapper wrapper = new ContextWrapper(context);
        // Construct an Intent that will execute the AlarmReceiver
        Intent alertIntent = new Intent(context, AlarmReceiver.class);

        scheduleAlarm(wrapper, alertIntent, null, "pref_alert_time", "22:00", ACTION_ALERT, false);
    }

    public static void run(Context context) {
        ContextWrapper wrapper = new ContextWrapper(context);
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());

        SDLog.d(DEBUG_TAG, "Run got called.\nScheduling both notification and alert alarms.");

        String alertTime = data.getString("pref_alert_time", "22:00");
        int alertHour = TimeUtilities.getHour(alertTime);
        int alertMinute = TimeUtilities.getMinute(alertTime);
        Calendar alertCalendarTime = Calendar.getInstance();
        alertCalendarTime.set(Calendar.HOUR_OF_DAY, alertHour);
        alertCalendarTime.set(Calendar.MINUTE, alertMinute);
        long alertMillis = alertCalendarTime.getTimeInMillis();

        String notificationTime = data.getString("pref_notification_time", "21:00");
        int notificationHour = TimeUtilities.getHour(notificationTime);
        int notificationMinute = TimeUtilities.getMinute(notificationTime);
        Calendar notificationCalendarTime = Calendar.getInstance();
        notificationCalendarTime.set(Calendar.HOUR_OF_DAY, notificationHour);
        notificationCalendarTime.set(Calendar.MINUTE, notificationMinute);
        long notificationMillis = notificationCalendarTime.getTimeInMillis();

        Calendar rightNow = Calendar.getInstance();
        long rightNowMillis = rightNow.getTimeInMillis();

        /*
            If the current time is before the notification time, schedule both.
            If the current time is after the notification time && before the alert time then
                if the current state is active
                    schedule both, send the notification.
                if the state is inactive
                    schedule the alert,
                    schedule the notification for its stored time on the next day.
            else if the current time is after both
                schedule the notification for its stored time on the next day
                schedule the alert
         */
        if (rightNowMillis < notificationMillis) {
            scheduleNotification(context, false);
            scheduleAlert(context);
        } else if (rightNowMillis > notificationMillis && rightNowMillis < alertMillis) {
            if (data.getInt("state", SWITCH_INACTIVE) == SWITCH_ACTIVE) {
                scheduleNotification(context, false);
                scheduleAlert(context);
                // Send the notification
            } else {
                scheduleNotification(context, true);
                scheduleAlert(context);
            }
        } else {
            scheduleNotification(context, true);
            scheduleAlert(context);
        }
    }
}
