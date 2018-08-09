package com.github.carlhmitchell.failsafealert.utilities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.carlhmitchell.failsafealert.R;


class MessageBuilder {
    private static final String DEBUG_TAG = "MessageBuilder";

    public static String buildMessage(Context context, boolean isTest) {
        final SharedPreferences sharedPref;

        SDLog.v(DEBUG_TAG, "Building a message.");

        ContextWrapper wrapper = new ContextWrapper(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());
        String timestamp = TimeUtilities.getFormattedTime();
        if (isTest) {
            // Build the test message.
            return wrapper.getString(R.string.test_message) +
                   "\nSent at " + timestamp;
        } else {
            // Build the real message.
            return sharedPref.getString("pref_message", "default message, this should never be seen") +
                   "\nSent at " + timestamp;
        }
    }
}
