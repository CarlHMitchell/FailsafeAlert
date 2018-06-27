package com.example.sai.passivewarningalarm;

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

import com.codinguser.android.contactpicker.ContactsPickerActivity;
import com.example.sai.passivewarningalarm.contactsStorage.AppDatabase;
import com.example.sai.passivewarningalarm.contactsStorage.Contact;
import com.example.sai.passivewarningalarm.contactsStorage.ContactRepository;
import com.example.sai.passivewarningalarm.contactsStorage.utils.DatabaseInitializer;
import com.example.sai.passivewarningalarm.settings.SettingsActivity;
import com.example.sai.passivewarningalarm.utilities.MessageSender;

import java.util.ArrayList;

import static com.example.sai.passivewarningalarm.utilities.AppConstants.DBG_CHANNEL_ID;
import static com.example.sai.passivewarningalarm.utilities.AppConstants.SWITCH_ACTIVE;
import static com.example.sai.passivewarningalarm.utilities.AppConstants.SWITCH_INACTIVE;

public class MainActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "MainActivity";
    //private Toolbar toolbar;
    private SharedPreferences data;
    private SharedPreferences.Editor editor;
    // For testing only.
    private AppDatabase dbgDb;

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

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlert();
            }
        });

        Button emailTestButton = findViewById(R.id.emailTestButton);
        emailTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testEmail();
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

        // Note: dB references should not be in an activity. Use the Repository.
        dbgDb = AppDatabase.getTestInMemoryDatabase(getApplicationContext());
        testPopulateDb();
    }

    private void testPopulateDb() {
        DatabaseInitializer.populateSync(dbgDb);
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
            notificationManager.createNotificationChannel(channel);
        }

    }

    private void testEmail() {
        Log.i("MainActivity", "Test Message button clicked");
        try {
            new MessageSender(this).sendMessages();
        } catch (Exception e) {
            Log.e("TestMessageButton", "Error creating MessageSender: " + e);
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
            Log.i("MainActivity", "Cancel Button clicked while switch is active");
            editor.putInt("state", SWITCH_INACTIVE);
            editor.apply();
            Intent cancelNotificationIntent = new Intent(this, BackgroundService.class);
            cancelNotificationIntent.putExtra("type", "cancelNotification");
            this.startService(cancelNotificationIntent);
        } else if (state == SWITCH_INACTIVE) {
            Log.i("MainActivity", "Cancel button clicked while switch inactive.");
            Log.i("MainActivity", "State is " + state);
        }
    }

    private void doLaunchContactEditor() {
        startActivity(new Intent(this, ContactEditorActivity.class));
    }

    private void doLaunchContactPhonePicker(View view) {
        startActivityForResult(new Intent(this, ContactsPickerActivity.class), ContactsPickerActivity.REQUEST_CONTACT_PHONE);
        //startActivity(new Intent(this, ContactEditorActivity.class));
    }

    private void doLaunchContactEmailPicker(View view) {

        startActivityForResult(new Intent(this, ContactsPickerActivity.class), ContactsPickerActivity.REQUEST_CONTACT_EMAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ContactsPickerActivity.REQUEST_CONTACT_PHONE:
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Result canceled", Toast.LENGTH_SHORT).show();
                    } else {
                        String phoneNumber = (String) data.getExtras().get(ContactsPickerActivity.KEY_PHONE_NUMBER);
                        String contactName = (String) data.getExtras().get(ContactsPickerActivity.KEY_CONTACT_NAME);


                        Log.i(DEBUG_TAG, "Phone number found: " + phoneNumber);
                        Log.i(DEBUG_TAG, "Contact name found: " + contactName);

                        ArrayList<String> phoneList = new ArrayList<String>();
                        phoneList.add(phoneNumber);

                        Contact contact = new Contact(contactName, phoneList, null);
                        ContactRepository repository = new ContactRepository(getApplication());
                        repository.insert(contact);

                    }
                    break;
                case ContactsPickerActivity.REQUEST_CONTACT_EMAIL:
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Result canceled", Toast.LENGTH_SHORT).show();
                    } else {
                        String emailAddress = (String) data.getExtras().get(ContactsPickerActivity.KEY_CONTACT_EMAIL);
                        String contactName = (String) data.getExtras().get(ContactsPickerActivity.KEY_CONTACT_NAME);

                        Log.i(DEBUG_TAG, "Email found: " + emailAddress);
                        Log.i(DEBUG_TAG, "Contact name found: " + contactName);

                        ArrayList<String> emailList = new ArrayList<String>();
                        emailList.add(emailAddress);

                        Contact contact = new Contact(contactName, null, emailList);
                        ContactRepository repository = new ContactRepository(getApplication());
                        repository.insert(contact);
                    }
                    break;
                default:
                    Log.e(DEBUG_TAG, "INVALID RESULT CODE: " + resultCode);
                    break;
            }
        } else {
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
    }


}
