package com.example.sai.passivewarningalarm.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.example.sai.passivewarningalarm.R;

public class SettingsActivity extends AppCompatActivity {
    private Button mailSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display the SettingsFragment as the main content
        /*
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        */
    }
}
