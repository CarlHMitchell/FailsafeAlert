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

import java.lang.ref.WeakReference;

public class MailSenderTask extends AsyncTask<String, Void, Boolean> {
    private final String DEBUG_TAG = "MailSenderTask";
    private final SharedPreferences sharedPref;
    private WeakReference<Context> contextWeakReference;

    public MailSenderTask(Context context) {
        contextWeakReference = new WeakReference<>(context);
        ContextWrapper wrapper = new ContextWrapper(contextWeakReference.get());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());
        SDLog.v(DEBUG_TAG, "MailSenderTask instantiated");
    }


    @Override
    protected Boolean doInBackground(String... params) {
        String recipient = params[0];
        String message = params[1];
        String mailhost;
        boolean auth;
        int port;
        int sslport;
        boolean fallback;
        boolean quitwait;


        try {
            SDLog.d(DEBUG_TAG, "About to instantiate email sender.");
            SDLog.d(DEBUG_TAG, "Recipient: " + recipient);

            mailhost = sharedPref.getString("pref_mail_mailhost", "");
            auth = sharedPref.getBoolean("pref_mail_auth", true);
            port = sharedPref.getInt("pref_mail_port", 465);
            sslport = sharedPref.getInt("pref_mail_sslport", 465);
            fallback = sharedPref.getBoolean("pref_mail_fallback", false);
            quitwait = sharedPref.getBoolean("pref_mail_quitwait", false);
            String username = sharedPref.getString("pref_mail_username", "");
            String password = sharedPref.getString("pref_mail_password", "");
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
            NotificationHelper helper = new NotificationHelper(contextWeakReference.get());
            helper.sendNotification(contextWeakReference.get().getString(R.string.email_send_error_notification_title),
                                    contextWeakReference.get().getString(R.string.email_send_error_notification_text));
        }
    }
}
