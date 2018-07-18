package com.example.sai.passivewarningalarm.settings.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class MailPresetPreference extends ListPreference {

    private String protocol;
    private String mailhost;
    private boolean auth;
    private boolean fallback;
    private boolean quitwait;
    private int port;
    private int sslport;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public MailPresetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            // User selected a value
            String value = getValue();
            //TODO: set all this to come from a custom XML file.
            switch (value) {
                case "Gmail":
                    protocol = "smtp";
                    mailhost = "smtp.gmail.com";
                    auth = true;
                    port = 465;
                    sslport = 465;
                    fallback = false;
                    quitwait = false;
                    break;
                case "Yahoo":
                    protocol = "smtp";
                    mailhost = "smtp.mail.yahoo.com";
                    auth = true;
                    port = 465;
                    sslport = 465;
                    fallback = false;
                    quitwait = false;
                    break;
                case "Live":
                    protocol = "smtp";
                    mailhost = "smtp.live.com";
                    auth = true;
                    port = 25;
                    sslport = 25;
                    fallback = false;
                    quitwait = false;
                    break;
                case "Verizon":
                    protocol = "smtp";
                    mailhost = "smtp.verizon.net";
                    auth = true;
                    port = 465;
                    sslport = 465;
                    fallback = false;
                    quitwait = false;
                    break;
                case "ATT":
                    protocol = "smtp";
                    mailhost = "smtp.att.yahoo.com";
                    auth = true;
                    port = 465;
                    sslport = 465;
                    fallback = false;
                    quitwait = false;
                    break;
                case "Comcast":
                    protocol = "smtp";
                    mailhost = "smtp.comcast.net";
                    auth = true;
                    port = 587;
                    sslport = 587;
                    fallback = false;
                    quitwait = false;
                    break;
                case "Cox":
                    protocol = "smtp";
                    mailhost = "smtp.cox.net";
                    auth = true;
                    port = 587;
                    sslport = 587;
                    fallback = false;
                    quitwait = false;
                    break;
                case "Frontier":
                    protocol = "smtp";
                    mailhost = "smtp.frontier.com";
                    auth = true;
                    port = 465;
                    sslport = 465;
                    fallback = false;
                    quitwait = false;
                    break;
                case "Custom":
                    protocol = "smtp";
                    mailhost = prefs.getString("pref_mail_mailhost", "");
                    auth = prefs.getBoolean("pref_mail_auth", true);
                    port = prefs.getInt("pref_mail_port", 465);
                    sslport = prefs.getInt("pref_mail_sslport", 465);
                    fallback = prefs.getBoolean("pref_mail_fallback", false);
                    quitwait = prefs.getBoolean("pref_mail_quitwait", false);
                    break;
                default:
                    protocol = "smtp";
                    mailhost = "smtp.gmail.com";
                    auth = true;
                    port = 465;
                    sslport = 465;
                    fallback = false;
                    quitwait = false;
                    break;
            }

            editor.putString("pref_mail_protocol", protocol);
            editor.putString("pref_mail_mailhost", mailhost);
            editor.putBoolean("pref_mail_auth", auth);
            editor.putBoolean("pref_mail_fallback", fallback);
            editor.putBoolean("pref_mail_quitwait", quitwait);
            editor.putInt("pref_mail_port", port);
            editor.putInt("pref_mail_sslport", sslport);
            editor.apply();
        }
    }
}
