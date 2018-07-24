package com.github.carlhmitchell.failsafealert.settings.preferences;

//ViewModel

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import com.github.carlhmitchell.failsafealert.R;
import com.github.carlhmitchell.failsafealert.utilities.EmailServerData;
import com.github.carlhmitchell.failsafealert.utilities.EmailServerDataXmlParser;

import java.util.ArrayList;

public class MailPresetPreference extends ListPreference {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final ArrayList<EmailServerData> servers;
    /*
    private static String server = "Custom";

    public static void setServer(final String newServer) {
        server = newServer;
    }
    */

    public MailPresetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();
        editor.apply();
        servers = EmailServerDataXmlParser.parse(context, R.xml.email_server_presets);
    }

    /*
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        setValue(server);
    }

    @Override
    protected void onSetInitialValue (boolean restoreValue, Object defaultValue) {
        super.onSetInitialValue(restoreValue, defaultValue);
        setValue(server);
    }
    */

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            // User selected a value
            String value = getValue();
            //setServer(value);
            boolean presetFound = false;
            for (EmailServerData serverData : servers) {
                if (serverData.getServerName().equalsIgnoreCase(value)) {
                    presetFound = true;
                    editor.putString("pref_mail_protocol", serverData.getProtocol());
                    editor.putString("pref_mail_mailhost", serverData.getMailhost());
                    editor.putBoolean("pref_mail_auth", serverData.getAuth());
                    editor.putBoolean("pref_mail_fallback", serverData.getFallback());
                    editor.putBoolean("pref_mail_quitwait", serverData.getQuitwait());
                    editor.putInt("pref_mail_port", serverData.getPort());
                    editor.putInt("pref_mail_sslport", serverData.getSslport());
                    editor.apply();
                    break;
                }
            }
            if (!presetFound) {
                // Use custom data, or default to Gmail.
                editor.putString("pref_mail_protocol", "smtp");
                editor.putString("pref_mail_mailhost", prefs.getString("pref_mail_mailhost", ""));
                editor.putBoolean("pref_mail_auth", prefs.getBoolean("pref_mail_auth", true));
                editor.putBoolean("pref_mail_fallback", prefs.getBoolean("pref_mail_fallback", false));
                editor.putBoolean("pref_mail_quitwait", prefs.getBoolean("pref_mail_quitwait", false));
                editor.putInt("pref_mail_port", prefs.getInt("pref_mail_port", 465));
                editor.putInt("pref_mail_sslport", prefs.getInt("pref_mail_sslport", 465));
                editor.apply();
            }
        }
    }
}
