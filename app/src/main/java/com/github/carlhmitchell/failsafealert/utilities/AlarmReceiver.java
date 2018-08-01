package com.github.carlhmitchell.failsafealert.utilities;

//Model

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.carlhmitchell.failsafealert.BackgroundService;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int ALERT = 0;
    public static final int NOTIFICATION = 1;

    // Triggered by the Alarm periodically. Starts the Background Service to run its task.
    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("type");
        SDLog.d("AlarmReceiver", "Got alarm, type: " + type);

        Intent alarmIntent = new Intent(context, BackgroundService.class);
        alarmIntent.putExtra("type", type);
        context.startService(alarmIntent);
    }
}
