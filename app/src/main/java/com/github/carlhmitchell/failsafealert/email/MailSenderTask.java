package com.github.carlhmitchell.failsafealert.email;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class MailSenderTask extends AsyncTask {
    //private final Activity sendMailActivity;
    private final SharedPreferences sharedPref;
    private ProgressDialog statusDialog;


    public MailSenderTask(Context context) {
        //sendMailActivity = activity;
        //ContextWrapper wrapper = new ContextWrapper(sendMailActivity);
        ContextWrapper wrapper = new ContextWrapper(context);
        //sharedPref = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());
    }
    /*
    public MailSenderTask() {
        final Activity sendMailActivity = new Activity();
        ContextWrapper wrapper = new ContextWrapper(sendMailActivity);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());
    }
    */

    protected void onPreExecute() {
        /*
        statusDialog = new ProgressDialog(sendMailActivity);
        statusDialog.setMessage("Pre-executing");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();
        */
    }


    @SuppressWarnings("SameReturnValue")
    protected Object realDoInBackground(String recipient) {
        String mailhost;
        boolean auth;
        int port;
        int sslport;
        boolean fallback;
        boolean quitwait;

        try {
            Log.d("MailSenderTask", "About to instantiate email sender.");
            try {
                publishProgress("Processing input");
            } catch (Exception e) {
                Log.e("Mail Sender", "Got exception publishing progress: " + e);
            }

            mailhost = sharedPref.getString("pref_mail_mailhost", "");
            auth = sharedPref.getBoolean("pref_mail_auth", true);
            port = sharedPref.getInt("pref_mail_port", 465);
            sslport = sharedPref.getInt("pref_mail_sslport", 465);
            fallback = sharedPref.getBoolean("pref_mail_fallback", false);
            quitwait = sharedPref.getBoolean("pref_mail_quitwait", false);
            String username = sharedPref.getString("pref_mail_username", "");
            String password = sharedPref.getString("pref_mail_password", "");
            String message = sharedPref.getString("pref_message", "default message, this should never be seen");
            MailSender sender = new MailSender.MailSenderBuilder(username, password)
                    .mailhost(mailhost)
                    .auth(String.valueOf(auth))
                    .port(String.valueOf(port))
                    .sslPort(String.valueOf(sslport))
                    .fallback(String.valueOf(fallback))
                    .quitwait(String.valueOf(quitwait))
                    .build();
            sender.sendMail("test subject", //Subject
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


    @Override
    protected Object doInBackground(Object... recipient) {
        return realDoInBackground(recipient[0].toString());
    }

    @Override
    public void onProgressUpdate(Object... values) {
        //statusDialog.setMessage(values[0].toString());
    }

    @Override
    public void onPostExecute(Object result) {
        //statusDialog.dismiss();
    }
}
