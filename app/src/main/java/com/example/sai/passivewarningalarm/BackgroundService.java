package com.example.sai.passivewarningalarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.sai.passivewarningalarm.utilities.AlarmReceiver;
import com.example.sai.passivewarningalarm.utilities.MessageSender;
import com.example.sai.passivewarningalarm.utilities.ScheduleAlarms;
import com.example.sai.passivewarningalarm.utilities.WakefulIntentService;

import java.util.Calendar;

import static android.app.PendingIntent.getBroadcast;
import static com.example.sai.passivewarningalarm.utilities.AppConstants.DBG_CHANNEL_ID;
import static com.example.sai.passivewarningalarm.utilities.AppConstants.MINUTE_MILLIS;
import static com.example.sai.passivewarningalarm.utilities.AppConstants.SWITCH_ACTIVE;
import static com.example.sai.passivewarningalarm.utilities.AppConstants.SWITCH_INACTIVE;

/**
 *
 */
public class BackgroundService extends WakefulIntentService {
    private final long TIME_DELTA_MILLIS;
    private final long MISSED_NOTIFICATION_DELTA_MILLIS;

    public BackgroundService() {
        super("BackgroundService");
        Log.i("BackgroundService", "Service running");
        TIME_DELTA_MILLIS = MINUTE_MILLIS * 1; // 1 minutes in milliseconds.
        MISSED_NOTIFICATION_DELTA_MILLIS = MINUTE_MILLIS * 15; // 15 minutes in milliseconds.
    }


    private void sendNotification() {
        // Get an instance of NotificationManager
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, DBG_CHANNEL_ID)
                .setSmallIcon(R.drawable.example_picture)
                .setContentTitle("Cancel the alert!")
                .setContentText("The Cancel button is active!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Notifications can be updated, instead of just replaced. They need an ID for this.
        mNotificationManager.notify(1, mBuilder.build());

    }

    private void cancelNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        ContextWrapper wrapper = new ContextWrapper(this);
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());
        SharedPreferences.Editor editor = data.edit();

        Log.i("BackgroundService", "Got Intent");

        int state = data.getInt("state", 0);
        long lastNotificationTime = data.getLong("last_notification_time", 0);
        Calendar rightNow = Calendar.getInstance();


        switch (intent.getStringExtra("type")) {
            case "notification":
                Log.i("BackgroundService", "Got notification intent. State is " + state);
                editor.putLong("last_notification_time", rightNow.getTimeInMillis());
                editor.apply();
                if (state == SWITCH_INACTIVE) {
                    // Switch was off & alarm received. Turn switch on & save state.
                    editor.putInt("state", SWITCH_ACTIVE);
                    editor.apply();
                    Log.i("BackgroundService", "Got notification alarm. Button Active.");
                    sendNotification();
                } else if (state == SWITCH_ACTIVE) {
                    editor.putInt("state", SWITCH_ACTIVE);
                    editor.apply();
                    Log.e("BackgroundService", "Got notification alarm while button active.\nThis should never happen.");
                }
                break;
            case "alert":
                if (state == SWITCH_INACTIVE) {
                    // Switch had been pressed, so don't send any alert.
                    Log.i("BackgroundService", "Got alert alarm. Button has been pressed. \n Doing nothing.");
                } else if (state == SWITCH_ACTIVE) {
                    Log.i("BackgroundService", "lastNotificationTime: " + lastNotificationTime);
                    Log.i("BackgroundService", "lastNotificationTime+TIME_DELTA: " + (lastNotificationTime + TIME_DELTA_MILLIS));
                    Log.i("BackgroundService", "rightNow.getTimeInMillis(): " + rightNow.getTimeInMillis());

                    long timeSinceLastNotification = (rightNow.getTimeInMillis() - lastNotificationTime);
                    Log.i("BackgroundService", "Time since last notification: " + timeSinceLastNotification);
                    if (timeSinceLastNotification < TIME_DELTA_MILLIS) {
                        // This block is to check for the condition when the phone is rebooted and
                        //    both alarms fire in rapid succession. It also guarantees a minimum
                        //    time between the two alarms.
                        // Got alert alarm within TIME_DELTA minutes of notification.
                        //     Ignore the alert, leave the switch active, and set a 1-shot alarm
                        //     for MISSED_NOTIFICATION_DELTA minutes from now.
                        Log.i("BackgroundService", "Got alert alarm, but time too close to notification. \n Adding " + (MISSED_NOTIFICATION_DELTA_MILLIS / 1000) / 60 + " minutes.");
                        Intent alertIntent = new Intent(this, AlarmReceiver.class);
                        alertIntent.putExtra("type", "alert");
                        // Create a PendingIntent to be triggered when the alarm goes off
                        // The PendingIntent.FLAG_UPDATE_CURRENT means that if the alarm fires quickly the events replace each other rather than stack up.
                        PendingIntent alertPendingIntent = getBroadcast(this, AlarmReceiver.ALERT, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                        long nextAlertTime = SystemClock.elapsedRealtime() + MISSED_NOTIFICATION_DELTA_MILLIS;
                        alarm.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextAlertTime, alertPendingIntent);
                    } else {
                        MessageSender sender = new MessageSender(this, this.getApplication());
                        Log.i("BackgroundService", "Got alert alarm. SEND ALERT HERE!");
                        //sender.sendMessages();
                        editor.putInt("state", SWITCH_INACTIVE);
                        editor.apply();
                    }
                }
                break;
            case "boot":
                Log.i("BackgroundService", "Got Intent from BootReceiver");
                ScheduleAlarms.run(getApplicationContext());
                break;
            case "startup":
                Log.i("BackgroundService", "Got Intent from app startup");
                ScheduleAlarms.run(getApplicationContext());
                break;
            case "cancelNotification":
                Log.i("BackgroundService", "Got Intent to cancel notification");
                cancelNotification();
                break;
            default:
                Log.e("BackgroundService", "Got unexpected Intent.");
                Log.e("BackgroundService", intent.toString());
                Log.e("BackgroundService", intent.getStringExtra("type"));
                break;
        }
        super.onHandleIntent(intent);
    }
}
