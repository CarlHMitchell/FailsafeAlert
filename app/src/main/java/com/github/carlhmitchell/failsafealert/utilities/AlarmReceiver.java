package com.github.carlhmitchell.failsafealert.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.carlhmitchell.failsafealert.BackgroundService;

public class AlarmReceiver extends BroadcastReceiver {
    //public static final int REQUEST_CODE = 12345;
    public static final int ALERT = 0;
    public static final int NOTIFICATION = 1;
    //public static final String ACTION = "com.example.sai.passivewarningalarm.alarm";

    // Triggered by the Alarm periodically. Starts the Background Service to run its task.
    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("type");
        Log.d("AlarmReceiver", "Got alarm, type: " + type);

        Log.d("AlarmReceiver", "Resetting alarms");
        //ScheduleAlarms.run(context);

        Intent alarmIntent = new Intent(context, BackgroundService.class);
        alarmIntent.putExtra("type", type);
        context.startService(alarmIntent);
        //WakefulIntentService.acquireStaticLock(context);

    }
}
