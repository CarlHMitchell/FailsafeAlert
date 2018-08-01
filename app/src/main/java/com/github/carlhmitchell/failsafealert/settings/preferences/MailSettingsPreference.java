package com.github.carlhmitchell.failsafealert.settings.preferences;

//ViewModel

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.github.carlhmitchell.failsafealert.R;
import com.github.carlhmitchell.failsafealert.utilities.SDLog;
import com.github.carlhmitchell.failsafealert.utilities.ToastHelper;

import java.net.URI;
import java.net.URISyntaxException;

class MailSettingsPreference extends DialogPreference {
    private final String DEBUG_TAG = MailSettingsPreference.class.getSimpleName();
    //private String protocol;
    //private EditText protocolET;
    private String mailhost;
    private EditText mailhostET;
    private boolean auth;
    private Switch authS;
    private boolean fallback;
    private Switch fallbackS;
    private boolean quitwait;
    private Switch quitwaitS;
    private int port;
    private EditText portET;
    private int sslport;
    private EditText sslportET;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;


    @SuppressWarnings({"WeakerAccess", "unused"})
    @SuppressLint("CommitPrefEdits")
    public MailSettingsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();

        setDialogLayoutResource(R.layout.email_settings_dialog_preference);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    /**
     * Inflates the dialog, binds the EditTexts to their Views, and sets their text to the current
     * values stored in the SharedPreferences
     * @return the created View.
     */
    @Override
    protected View onCreateDialogView() {
        // Suppressing Lint for null view root. According to
        //  http://www.doubleencore.com/2013/05/layout-inflation-as-intended/
        //  the case of inflating a dialog is one of the only ones where passing a null view root
        //  is the correct thing to do.
        @SuppressLint("InflateParams") View v = LayoutInflater.from(getContext()).inflate(R.layout.email_settings_dialog_preference, null, false);

        //Protocol is hardcoded to SMTP at the moment.
        //protocolET = v.findViewById(R.id.mail_protocol_edit_text);
        //protocolET.setText(prefs.getString("pref_mail_protocol", "smtp"));

        mailhostET = v.findViewById(R.id.mail_host_edit_text);
        mailhostET.setText(prefs.getString("pref_mail_mailhost", "smtp.gmail.com"));

        authS = v.findViewById(R.id.mail_auth_switch);
        authS.setChecked(prefs.getBoolean("pref_mail_auth", true));

        fallbackS = v.findViewById(R.id.mail_fallback_switch);
        fallbackS.setChecked(prefs.getBoolean("pref_mail_fallback", false));

        quitwaitS = v.findViewById(R.id.mail_quitwait_switch);
        quitwaitS.setChecked(prefs.getBoolean("pref_mail_quitwait", false));

        portET = v.findViewById(R.id.mail_port_edit_number);
        portET.setText(String.valueOf(prefs.getInt("pref_mail_port", 465)));

        sslportET = v.findViewById(R.id.mail_sslport_edit_number);
        sslportET.setText(String.valueOf(prefs.getInt("pref_mail_sslport", 465)));

        return v;
    }

    /**
     * Persist new values to the SharedPreferences if the user pressed OK.
     * @param positiveResult true if the user pressed OK.
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // The user pressed OK. Persist the new values.
            //protocol = protocolET.getText().toString();
            //editor.putString("pref_mail_protocol", protocol);

            mailhost = mailhostET.getText().toString();
            try {
                URI mailhostURI = new URI(mailhost);
                SDLog.d(DEBUG_TAG, mailhostURI.toString()); // This should really just be an annotation
                                                          // to suppress the warning.
                editor.putString("pref_mail_mailhost", mailhost);
            } catch (URISyntaxException e) {
                ToastHelper.toast(this.getContext(), "Error, invalid mailhost URL", 0);
                SDLog.e(DEBUG_TAG, "Caught exception:\n" + e);
                SDLog.e(DEBUG_TAG, "URL :" + mailhost);
            }

            auth = authS.isChecked();
            editor.putBoolean("pref_mail_auth", auth);

            fallback = fallbackS.isChecked();
            editor.putBoolean("pref_mail_fallback", fallback);

            quitwait = quitwaitS.isChecked();
            editor.putBoolean("pref_mail_quitwait", quitwait);

            port = Integer.parseInt(portET.getText().toString());
            editor.putInt("pref_mail_port", port);

            sslport = Integer.parseInt(sslportET.getText().toString());
            editor.putInt("pref_mail_sslport", sslport);

            editor.apply();
            //MailPresetPreference.setServer("Custom");
        }
    }

    /**
     * Show the correct default values when opened.
     * @param restorePersistedValue true if the SharedPreferences should be read to find the current value.
     * @param defaultValue used if the SharedPreferences doesn't have a value stored.
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        //protocol = prefs.getString("pref_mail_protocol", "smtp");
        mailhost = prefs.getString("pref_mail_mailhost", "smtp.gmail.com");
        auth = prefs.getBoolean("pref_mail_auth", true);
        fallback = prefs.getBoolean("pref_mail_fallback", false);
        quitwait = prefs.getBoolean("pref_mail_quitwait", false);
        port = prefs.getInt("pref_mail_port", 465);
        sslport = prefs.getInt("pref_mail_sslport", 465);
    }
}
