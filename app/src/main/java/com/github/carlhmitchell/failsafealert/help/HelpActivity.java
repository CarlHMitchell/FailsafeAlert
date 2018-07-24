package com.github.carlhmitchell.failsafealert.help;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.carlhmitchell.failsafealert.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        final Context context = this;

        Button contactsHelpButton = findViewById(R.id.contacts_help_button);
        contactsHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContactsHelpActivity.class);
                startActivity(intent);
            }
        });

        Button settingsHelpButton = findViewById(R.id.settings_help_button);
        settingsHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsHelpActivity.class);
                startActivity(intent);
            }
        });

        Button emailSettingsHelpButton = findViewById(R.id.email_settings_help_button);
        emailSettingsHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EmailSettingsHelpActivity.class);
                startActivity(intent);
            }
        });
    }
}
