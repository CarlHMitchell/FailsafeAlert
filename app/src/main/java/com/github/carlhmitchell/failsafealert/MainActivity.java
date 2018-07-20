package com.github.carlhmitchell.failsafealert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.carlhmitchell.contactablespicker.ContactsList;
import com.github.carlhmitchell.failsafealert.settings.SettingsActivity;
import com.github.carlhmitchell.failsafealert.utilities.MessageSender;
import com.github.carlhmitchell.failsafealert.utilities.ToastService;

import java.util.Objects;

import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.DBG_CHANNEL_ID;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.SWITCH_ACTIVE;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.SWITCH_INACTIVE;

public class MainActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "MainActivity";
    //private Toolbar toolbar;
    private SharedPreferences data;
    private SharedPreferences.Editor editor;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar); // Attach the layout to the Toolbar object
        setSupportActionBar(toolbar); // Set the toolbar as the ActionBar for backwards compatibility

        // Set the preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);


        ContextWrapper wrapper = new ContextWrapper(this);
        data = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());
        editor = data.edit();
        editor.apply();

        cancelButton = findViewById(R.id.cancelButton);
        int state = data.getInt("state", 0);
        if (state == SWITCH_ACTIVE) {
            enableCancelButton();
        } else if (state == SWITCH_INACTIVE) {
            disableCancelButton();
        }

        Button emailTestButton = findViewById(R.id.emailTestButton);
        emailTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testMessage();
            }
        });

        //+ button, should launch contacts? Or just put this in the Settings activity?
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //doLaunchContactPhonePicker(view);
                doLaunchContactEditor();
                //Snackbar.make(view, R.string.contact_selected, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        //Context context; // android.content.Context
        //context = getApplicationContext();
        BackgroundService.acquireStaticLock(this);
        Intent startupIntent = new Intent(this, BackgroundService.class);
        startupIntent.putExtra("type", "startup");
        this.startService(startupIntent);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "dbg_channel_name";
            String description = "dbg_channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(DBG_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

    }

    private void enableCancelButton() {
        cancelButton.setText(R.string.cancel_button_enabled);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlert();
                ToastService.toast(getBaseContext(), getString(R.string.alert_canceled), Toast.LENGTH_SHORT);
            }
        });
    }

    private void disableCancelButton() {
        cancelButton.setText(R.string.cancel_button_disabled);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastService.toast(getBaseContext(), getString(R.string.cancel_button_disabled_message), Toast.LENGTH_SHORT);
            }
        });
    }

    private void testMessage() {
        Log.i(DEBUG_TAG, "Test Message button clicked");
        try {
            new MessageSender(this).sendMessages();
            ToastService.toast(this, "Alert sent to selected contacts", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Log.e("TestMessageButton", "Error creating MessageSender: " + e);
            ToastService.toast(this, "Error, could not send alert!", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            settingsMessage();
            return true; // Open settings activity
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the user taps the settings button
     */
    private void settingsMessage() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void cancelAlert() {
        int state = data.getInt("state", 0);
        if (state == SWITCH_ACTIVE) {
            Log.i(DEBUG_TAG, "Cancel Button clicked while switch is active");
            editor.putInt("state", SWITCH_INACTIVE);
            editor.apply();
            disableCancelButton();
            Intent cancelNotificationIntent = new Intent(this, BackgroundService.class);
            cancelNotificationIntent.putExtra("type", "cancelNotification");
            this.startService(cancelNotificationIntent);
        } else if (state == SWITCH_INACTIVE) {
            Log.i(DEBUG_TAG, "Cancel button clicked while switch inactive.");
            Log.i(DEBUG_TAG, "State is " + state);
        }

    }

    private void doLaunchContactEditor() {
        startActivity(new Intent(this, ContactsList.class));
    }
}
