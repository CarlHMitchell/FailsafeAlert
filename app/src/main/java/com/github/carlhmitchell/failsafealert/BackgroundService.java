package com.github.carlhmitchell.failsafealert;

//Model?

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.github.carlhmitchell.failsafealert.utilities.background.AlarmReceiver;
import com.github.carlhmitchell.failsafealert.utilities.MessageSender;
import com.github.carlhmitchell.failsafealert.utilities.SDLog;
import com.github.carlhmitchell.failsafealert.utilities.ScheduleAlarms;
import com.github.carlhmitchell.failsafealert.utilities.background.WakefulIntentService;

import java.util.Calendar;
import java.util.Objects;

import static android.app.PendingIntent.getBroadcast;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.ACTION_ALERT;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.ACTION_CANCEL_NOTIFICATION;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.ACTION_NOTIFICATION;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.ACTION_STARTUP;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.NOTIFICATION_CHANNEL_ID;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.MINUTE_MILLIS;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.SWITCH_ACTIVE;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.SWITCH_INACTIVE;

/**
 * Service to handle notifying the user when a notification alert is recieved, send the alerts when
 *  the alarm alert is recieved, and maintain a state machine to track what action is needed.
 */
public class BackgroundService extends WakefulIntentService {
    private final long TIME_DELTA_MILLIS;
    private final long MISSED_NOTIFICATION_DELTA_MILLIS;
    private final String DEBUG_TAG = BackgroundService.class.getSimpleName();

    public BackgroundService() {
        super("BackgroundService");
        SDLog.i(DEBUG_TAG, "Service running");
        //noinspection PointlessArithmeticExpression
        TIME_DELTA_MILLIS = MINUTE_MILLIS * 1; // 1 minutes in milliseconds.
        MISSED_NOTIFICATION_DELTA_MILLIS = MINUTE_MILLIS * 15; // 15 minutes in milliseconds.
    }


    /**
     * Sends a notification to the user so they can cancel the alert from being sent.
     */
    private void sendNotification() {
        // Get an instance of NotificationManager
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder;
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                    .setContentTitle(getString(R.string.cancel_notification_title))
                    .setContentText(getString(R.string.cancel_notification_text))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setSound(uri)
                    .setLights(getColor(R.color.notificationLight), 250, 250);
        } else {

            mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                    .setContentTitle(getString(R.string.cancel_notification_title))
                    .setContentText(getString(R.string.cancel_notification_text))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setSound(uri);
        }

        // Get an instance of the NotificationManager service
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Notifications can be updated, instead of just replaced. They need an ID for this.
        Objects.requireNonNull(mNotificationManager).notify(1, mBuilder.build());

    }

    /**
     * Clear the notification. Called when the user presses the "cancel alert" button.
     */
    private void cancelNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(mNotificationManager).cancelAll();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        ContextWrapper wrapper = new ContextWrapper(this);
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());
        SharedPreferences.Editor editor = data.edit();

        SDLog.i(DEBUG_TAG, "Got Intent");

        int state = data.getInt("state", 0);
        long lastNotificationTime = data.getLong("last_notification_time", 0);
        Calendar rightNow = Calendar.getInstance();


        switch (intent.getAction()) {
            case ACTION_NOTIFICATION:
                SDLog.i(DEBUG_TAG, "Got notification intent. State is " + state);
                editor.putLong("last_notification_time", rightNow.getTimeInMillis());
                editor.apply();
                if (state == SWITCH_INACTIVE) {
                    // Switch was off & alarm received. Turn switch on & save state.
                    editor.putInt("state", SWITCH_ACTIVE);
                    editor.apply();
                    SDLog.i(DEBUG_TAG, "Got notification alarm. Button Active.");
                    sendNotification();
                } else if (state == SWITCH_ACTIVE) {
                    editor.putInt("state", SWITCH_INACTIVE);
                    editor.apply();
                    SDLog.e(DEBUG_TAG, "Got notification alarm while button active.\n" +
                                               "This should never happen.\n" +
                                               "Deactivating");
                }
                break;
            case ACTION_ALERT:
                if (state == SWITCH_INACTIVE) {
                    // Switch had been pressed, so don't send any alert.
                    SDLog.i(DEBUG_TAG, "Got alert alarm. Button has been pressed. \n Doing nothing.");
                } else if (state == SWITCH_ACTIVE) {
                    SDLog.i(DEBUG_TAG, "lastNotificationTime: " + lastNotificationTime);
                    SDLog.i(DEBUG_TAG, "lastNotificationTime+TIME_DELTA: " + (lastNotificationTime + TIME_DELTA_MILLIS));
                    SDLog.i(DEBUG_TAG, "rightNow.getTimeInMillis(): " + rightNow.getTimeInMillis());

                    long timeSinceLastNotification = (rightNow.getTimeInMillis() - lastNotificationTime);
                    SDLog.i(DEBUG_TAG, "Time since last notification: " + timeSinceLastNotification);
                    if (timeSinceLastNotification < TIME_DELTA_MILLIS) {
                        // This block is to check for the condition when the phone is rebooted and
                        //    both alarms fire in rapid succession. It also guarantees a minimum
                        //    time between the two alarms.
                        // Got alert alarm within TIME_DELTA minutes of notification.
                        //     Ignore the alert, leave the switch active, and set a 1-shot alarm
                        //     for MISSED_NOTIFICATION_DELTA minutes from now.
                        SDLog.i(DEBUG_TAG, "Got alert alarm, but time too close to notification. \n Adding " + (MISSED_NOTIFICATION_DELTA_MILLIS / 1000) / 60 + " minutes.");
                        Intent alertIntent = new Intent(this, AlarmReceiver.class);
                        alertIntent.setAction(ACTION_ALERT);
                        // Create a PendingIntent to be triggered when the alarm goes off
                        // The PendingIntent.FLAG_UPDATE_CURRENT means that if the alarm fires quickly the events replace each other rather than stack up.
                        PendingIntent alertPendingIntent = getBroadcast(this, AlarmReceiver.ALERT, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                        long nextAlertTime = SystemClock.elapsedRealtime() + MISSED_NOTIFICATION_DELTA_MILLIS;
                        Objects.requireNonNull(alarm).setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextAlertTime, alertPendingIntent);
                    } else {
                        MessageSender sender = new MessageSender(this);
                        SDLog.i(DEBUG_TAG, "Got alert alarm. SEND ALERT HERE!");
                        sender.sendHelpRequest(false);
                        editor.putInt("state", SWITCH_INACTIVE);
                        editor.apply();
                        cancelNotification();
                    }
                }
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                SDLog.i(DEBUG_TAG, "Got Intent from BootReceiver");
                ScheduleAlarms.run(getApplicationContext());
                break;
            case ACTION_STARTUP:
                SDLog.i(DEBUG_TAG, "Got Intent from app startup");
                //ScheduleAlarms.run(getApplicationContext());
                break;
            case ACTION_CANCEL_NOTIFICATION:
                SDLog.i(DEBUG_TAG, "Got Intent to cancel notification");
                cancelNotification();
                break;
            default:
                SDLog.e(DEBUG_TAG, "Got unexpected Intent.");
                SDLog.e(DEBUG_TAG, intent.toString());
                SDLog.e(DEBUG_TAG, intent.getAction());
                //SDLog.e(DEBUG_TAG, intent.getStringExtra("type"));
                break;
        }
        super.onHandleIntent(intent);
    }
}
