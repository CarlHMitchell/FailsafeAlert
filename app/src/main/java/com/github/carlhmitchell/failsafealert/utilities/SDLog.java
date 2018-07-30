package com.github.carlhmitchell.failsafealert.utilities;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SDLog {
    private static String DEBUG_TAG = SDLog.class.getSimpleName();

    public static void printLog(Context context) {
        try {
            String filename = Objects.requireNonNull(context.getExternalFilesDir(null)).getPath() + File.separator + "failsafe_alert.log";
            String command = "logcat -f " + filename + " -v time -d *:V";

            Log.d(DEBUG_TAG, "command: " + command);

            try {
                Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
