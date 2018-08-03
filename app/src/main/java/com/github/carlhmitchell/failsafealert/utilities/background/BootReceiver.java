package com.github.carlhmitchell.failsafealert.utilities.background;

//Model

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.carlhmitchell.failsafealert.BackgroundService;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Starts BackgroundService when android.intent.action.BOOT_COMPLETED event triggers
        if (Objects.requireNonNull(intent.getAction()).equals("android.intent.action.BOOT_COMPLETED")) {
            // Set up the timers here.
            BackgroundService.acquireStaticLock(context);
            Intent bootIntent = new Intent(context, BackgroundService.class);
            bootIntent.setAction(Intent.ACTION_BOOT_COMPLETED);
            context.startService(bootIntent);
        }
    }
}
