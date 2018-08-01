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
@SuppressWarnings("ResultOfMethodCallIgnored")
public class SDLog {

    public static void printLog() {
        if (LOG_TO_SD) {
            try {
                String filename = Environment.getExternalStorageDirectory() + File.separator + "failsafe_alert.log";
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

    public static int v(String tag, String line) {
        if (LOG_TO_SD) {
            try {
                String filename = Environment.getExternalStorageDirectory() + File.separator + "failsafe_alert_lines.log";
                File file = new File(filename);
                file.createNewFile();
                FileWriter writer = new FileWriter(file, true);
                writer.write("V/" + tag + ": " + line + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Log.v(tag, line);
    }

    public static int d(String tag, String line) {
        if (LOG_TO_SD) {
            try {
                String filename = Environment.getExternalStorageDirectory() + File.separator + "failsafe_alert_lines.log";
                File file = new File(filename);
                file.createNewFile();
                FileWriter writer = new FileWriter(file, true);
                writer.write("D/" + tag + ": " + line + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Log.d(tag, line);
    }

    public static int i(String tag, String line) {
        if (LOG_TO_SD) {
            try {
                String filename = Environment.getExternalStorageDirectory() + File.separator + "failsafe_alert_lines.log";
                File file = new File(filename);
                file.createNewFile();
                FileWriter writer = new FileWriter(file, true);
                writer.write("D/" + tag + ": " + line + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Log.i(tag, line);
    }


    public static int w(String tag, String line) {
        if (LOG_TO_SD) {
            try {
                String filename = Environment.getExternalStorageDirectory() + File.separator + "failsafe_alert_lines.log";
                File file = new File(filename);
                file.createNewFile();
                FileWriter writer = new FileWriter(file, true);
                writer.write("W/" + tag + ": " + line + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Log.w(tag, line);
    }

    public static int e(String tag, String line) {
        if (LOG_TO_SD) {
            try {
                String filename = Environment.getExternalStorageDirectory() + File.separator + "failsafe_alert_lines.log";
                File file = new File(filename);
                file.createNewFile();
                FileWriter writer = new FileWriter(file, true);
                writer.write("E/" + tag + ": " + line + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Log.e(tag, line);
    }

    public static int wtf(String tag, String line) {
        if (LOG_TO_SD) {
            try {
                String filename = Environment.getExternalStorageDirectory() + File.separator + "failsafe_alert_lines.log";
                File file = new File(filename);
                file.createNewFile();
                FileWriter writer = new FileWriter(file, true);
                writer.write("F/" + tag + ": " + line + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Log.wtf(tag, line);
    }
}
