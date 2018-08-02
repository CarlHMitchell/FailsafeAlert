package com.github.carlhmitchell.failsafealert.email;

//??DI?

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.github.carlhmitchell.failsafealert.R;
import com.github.carlhmitchell.failsafealert.utilities.NotificationHelper;
import com.github.carlhmitchell.failsafealert.utilities.SDLog;

public class MailSenderTask extends AsyncTask<String, Void, Boolean> {
    private final String DEBUG_TAG = "MailSenderTask";
    private final SharedPreferences sharedPref;
    private String message;
    private ContextWrapper wrapper;

    public MailSenderTask(Context context) {
        wrapper = new ContextWrapper(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());
        message = wrapper.getString(R.string.test_message);
    }


    @Override
    protected Boolean doInBackground(String... params) {
        String recipient = params[0];
        boolean isTest = Boolean.parseBoolean(params[1]);
        String mailhost;
        boolean auth;
        int port;
        int sslport;
        boolean fallback;
        boolean quitwait;


        try {
            SDLog.d(DEBUG_TAG, "About to instantiate email sender.");
            SDLog.d(DEBUG_TAG, "Recipient: " + recipient);

            if (!isTest) {
                message = sharedPref.getString("pref_message", "default message, this should never be seen");
            }

            mailhost = sharedPref.getString("pref_mail_mailhost", "");
            auth = sharedPref.getBoolean("pref_mail_auth", true);
            port = sharedPref.getInt("pref_mail_port", 465);
            sslport = sharedPref.getInt("pref_mail_sslport", 465);
            fallback = sharedPref.getBoolean("pref_mail_fallback", false);
            quitwait = sharedPref.getBoolean("pref_mail_quitwait", false);
            String username = sharedPref.getString("pref_mail_username", "");
            String password = sharedPref.getString("pref_mail_password", "");
            //String message = sharedPref.getString("pref_message", "default message, this should never be seen");
            MailSender sender = new MailSender.MailSenderBuilder(username, password)
                    .mailhost(mailhost)
                    .auth(String.valueOf(auth))
                    .port(String.valueOf(port))
                    .sslPort(String.valueOf(sslport))
                    .fallback(String.valueOf(fallback))
                    .quitwait(String.valueOf(quitwait))
                    .build();
            boolean sendSuccess = sender.sendMail("Failsafe Alert!", //Subject
                            message, // Body
                            username, //From
                            recipient // To
            );
            SDLog.d(DEBUG_TAG, "Mail sent");
            return sendSuccess;
        } catch (Exception e) {
            SDLog.e(DEBUG_TAG, e.getMessage() + "\n" + e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (!result) {
            //Failure!
            NotificationHelper helper = new NotificationHelper(wrapper);
            helper.sendNotification(wrapper.getString(R.string.email_send_error_notification_title),
                                    wrapper.getString(R.string.email_send_error_notification_text));
        }
        wrapper = null;
    }
}
