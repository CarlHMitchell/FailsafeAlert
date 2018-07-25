package com.github.carlhmitchell.failsafealert.email;

//??DI?

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.carlhmitchell.failsafealert.R;

public class MailSenderTask extends AsyncTask<String, Void, Void> {
    private final SharedPreferences sharedPref;
    private String message;


    public MailSenderTask(Context context) {
        ContextWrapper wrapper = new ContextWrapper(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());
        message = wrapper.getString(R.string.test_message);
    }


    @Override
    protected Void doInBackground(String... params) {
        String recipient = params[0];
        boolean isTest = Boolean.parseBoolean(params[1]);
        String mailhost;
        boolean auth;
        int port;
        int sslport;
        boolean fallback;
        boolean quitwait;


        try {
            Log.d("MailSenderTask", "About to instantiate email sender.");

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
            sender.sendMail("Failsafe Alert!", //Subject
                            message, // Body
                            username, //From
                            recipient // To
            );
            Log.d("MailSender", "Mail sent");
        } catch (Exception e) {
            Log.e("MailSender", e.getMessage(), e);
        }
        return null;
    }
}
