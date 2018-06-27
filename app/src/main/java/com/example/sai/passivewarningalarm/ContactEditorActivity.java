package com.example.sai.passivewarningalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codinguser.android.contactpicker.ContactsPickerActivity;
import com.codinguser.android.contactpicker.OnContactSelectedListener;
import com.example.sai.passivewarningalarm.contactsStorage.ContactRepository;

// Very important that this extends AppCompatActivity instead of ContactsPickerActivity as in the sample.
public class ContactEditorActivity extends AppCompatActivity implements OnContactSelectedListener {

    private static final int GET_PHONE_NUMBER = ContactsPickerActivity.REQUEST_CONTACT_PHONE;
    private static final int GET_EMAIL = ContactsPickerActivity.REQUEST_CONTACT_EMAIL;
    private final static String DEBUG_TAG = "Contact Editor";
    ContactRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_editor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button buttonAddPhone = findViewById(R.id.add_Phone_Button);
        buttonAddPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLaunchContactPhonePicker(v);
            }
        });

        Button buttonAddEmail = findViewById(R.id.add_Email_Button);
        buttonAddEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLaunchContactEmailPicker(v);
            }
        });

        repository = new ContactRepository(this.getApplicationContext());
    }

    private void doLaunchContactPhonePicker(View view) {
        //Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
        //startActivityForResult(new Intent(this, ContactsPickerActivity.class), GET_PHONE_NUMBER);
        Intent phonePickerIntent = new Intent(this, ContactsPickerActivity.class);
        phonePickerIntent.putExtra(ContactsPickerActivity.ARG_PICKER_MODE, GET_PHONE_NUMBER);
        startActivity(phonePickerIntent);
    }

    private void doLaunchContactEmailPicker(View view) {
        //Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
        //startActivityForResult(new Intent(this, ContactsPickerActivity.class), GET_EMAIL);
        Intent phonePickerIntent = new Intent(this, ContactsPickerActivity.class);
        phonePickerIntent.putExtra(ContactsPickerActivity.ARG_PICKER_MODE, GET_EMAIL);
        startActivity(phonePickerIntent);
    }


    @Override
    public void onContactNameSelected(long contactID) {
        Toast.makeText(this, "Selected:\n " + contactID + "\n An intent would be delivered to your app",
                       Toast.LENGTH_LONG).show();
    }


    /**
     * Callback when the contact number is selected from the contact details view
     * Sets the activity result with the contact information and finishes
     */
    @Override
    public void onContactNumberSelected(String contactNumber, String contactName) {
        Toast.makeText(this, String.format("Selected:\n %s: %s\nAn intent would be delivered to your app",
                                           contactName, contactNumber),
                       Toast.LENGTH_LONG).show();
    }

    @Override
    public void onContactEmailSelected(String email, String contactName) {
        Toast.makeText(this, String.format("Selected:\n %s: <%s>\nAn intent would be delivered to your app",
                                           contactName, email),
                       Toast.LENGTH_LONG).show();
    }



    /*

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String phoneNumber;
        ArrayList phoneNumbers = new ArrayList();
        String emailAddress;
        ArrayList emailAddresses = new ArrayList();
        String contactName = "";
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GET_PHONE_NUMBER:
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Result canceled", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            phoneNumber = (String) data.getExtras().get(ContactsPickerActivity.KEY_PHONE_NUMBER);
                            phoneNumbers.add(phoneNumber);
                            Log.i(DEBUG_TAG, "Phone number found: " + phoneNumber);
                            Toast.makeText(this, "Phone number found: " + phoneNumber, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(DEBUG_TAG, "Phone number not found.");
                        }
                        try {
                            contactName = (String) data.getExtras().get(ContactsPickerActivity.KEY_CONTACT_NAME);
                            Log.i(DEBUG_TAG, "Contact name found: " + contactName);
                        } catch (Exception e) {
                            Log.e(DEBUG_TAG, "ERROR! CONTACT NAME NOT FOUND.");
                        }
                    }
                    break;
                case GET_EMAIL:
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Result canceled", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            emailAddress = (String) data.getExtras().get(ContactsPickerActivity.KEY_CONTACT_EMAIL);
                            emailAddresses.add(emailAddress);
                            Toast.makeText(this, "Email found: " + emailAddress, Toast.LENGTH_SHORT).show();
                            Log.i(DEBUG_TAG, "Email address found: " + emailAddress);
                        } catch (Exception e) {
                            Log.e(DEBUG_TAG, "Email address not found.");
                        }
                        try {
                            contactName = (String) data.getExtras().get(ContactsPickerActivity.KEY_CONTACT_NAME);
                            Log.i(DEBUG_TAG, "Contact name found: " + contactName);
                        } catch (Exception e) {
                            Log.e(DEBUG_TAG, "ERROR! CONTACT NAME NOT FOUND.");
                        }
                    }
                    break;
                default:
                    Log.e(DEBUG_TAG, "Unrecognized request code.");
                    break;
            }
            Contact contact = new Contact(contactName, phoneNumbers, emailAddresses);
            repository.insert(contact);
        } else {
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
    }

    */

}
