package com.github.carlhmitchell.failsafealert.utilities;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * A simple class to allow sending a Toast from any Context.
 */

public class ToastService {

    public ToastService() {}

    /**
     * Send a Toast from any Context, even a Service or Application!
     * @param context Context from which to send the Toast.
     * @param text Text of the toast.
     * @param length Toast.LENGTH_SHORT (0) or Toast.LENGTH_LONG (1).
     */
    public static void toast(final Context context, final String text, int length) {
        if (length != Toast.LENGTH_LONG) {
            length = Toast.LENGTH_SHORT;
        }
        final int toastLength = length;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, text, toastLength).show();
            }
        });
    }
}
