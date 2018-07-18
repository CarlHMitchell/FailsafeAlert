package com.example.sai.passivewarningalarm.settings.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.example.sai.passivewarningalarm.R;

public class MailSettingsPreference extends DialogPreference {
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
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    public MailSettingsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();

        setDialogLayoutResource(R.layout.email_settings_dialog_preference);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    protected View onCreateDialogView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.email_settings_dialog_preference, null);

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

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // The user pressed OK. Persist the new values.
            //protocol = protocolET.getText().toString();
            //editor.putString("pref_mail_protocol", protocol);

            mailhost = mailhostET.getText().toString();
            editor.putString("pref_mail_mailhost", mailhost);

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
        }
    }

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