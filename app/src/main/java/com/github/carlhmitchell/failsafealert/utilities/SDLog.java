package com.github.carlhmitchell.failsafealert.utilities;

import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.LOG_TO_SD;

// File.createNewFile() is used simply to ensure that the log file exists.
// Log() returns an int. I have had no reason to use these, but it's better to pass it through.
@SuppressWarnings({"ResultOfMethodCallIgnored", "UnusedReturnValue"})
public class SDLog {
    private static final String LOG_FILE_NAME = "failsafe_alert.log";
    private static final String LOG_LINES_NAME = "failsafe_alert_lines.log";

    /**
     * Runs logcat, outputting at the Verbose level. Saves output to a file on SD card.
     */
    public static void printLog() {
        if (LOG_TO_SD) {
            try {
                String filename = Environment.getExternalStorageDirectory() + File.separator + LOG_FILE_NAME;
                String command = "logcat -d *:V";
                Process process = Runtime.getRuntime().exec(command);

                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                try {
                    File file = new File(filename);
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file, true);
                    while ((line = in.readLine()) != null) {
                        writer.write(line + "\n");
                    }
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Log a message to disk, if LOG_TO_SD constant is true.
     * @param tag Debug tag (name of calling class, etc)
     * @param msg Message to print
     * @param type Type of msg (V, D, I, W, E, WTF)
     */
    private static void logLine(String tag, String msg, String type) {
        TimeFormatter timeFormatter = new TimeFormatter();
        String time = timeFormatter.getFormattedTime();
        if (LOG_TO_SD) {
            try {
                String filename = Environment.getExternalStorageDirectory() + File.separator + LOG_LINES_NAME;
                File file = new File(filename);
                file.createNewFile();
                FileWriter writer = new FileWriter(file, true);
                writer.write(time + ": " + type + "/" + tag + ": " + msg + "\n" );
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int v(String tag, String msg) {
        logLine(tag, msg, "V");
        return Log.v(tag, msg);
    }

    public static int d(String tag, String msg) {
        logLine(tag, msg, "D");
        return Log.d(tag, msg);
    }

    public static int i(String tag, String msg) {
        logLine(tag, msg, "I");
        return Log.i(tag, msg);
    }


    @SuppressWarnings("unused")
    //Just because there are no Warning level log statements now doesn't mean there won't be later.
    public static int w(String tag, String msg) {
        logLine(tag, msg, "W");
        return Log.w(tag, msg);
    }

    public static int e(String tag, String msg) {
        logLine(tag, msg, "E");
        return Log.e(tag, msg);
    }

    @SuppressWarnings("unused")
    //Just because there are no WTF level log statements now doesn't mean there won't be later.
    public static int wtf(String tag, String msg) {
        logLine(tag, msg, "WTF");
        return Log.wtf(tag, msg);
    }
}
