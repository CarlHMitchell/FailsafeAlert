package com.example.sai.passivewarningalarm.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.sai.passivewarningalarm.email.MailSenderTask;
import com.github.carlhmitchell.contactablespicker.Storage.Contact;
import com.github.carlhmitchell.contactablespicker.Storage.ContactRepository;
import com.github.carlhmitchell.contactablespicker.listViewHelpers.ContactsListAdapter;

import java.util.List;

import static com.example.sai.passivewarningalarm.utilities.AppConstants.PERMISSIONS_REQUEST_SEND_SMS;


public class MessageSender {
    private final Context messageSenderContext;
    private final SharedPreferences sharedPref;
    ContactsListAdapter mAdapter;
    private List<Contact> mAllContacts;

    public MessageSender(Context context) {
        messageSenderContext = context;


        // Shared Pref to get the message.
        // TODO: Move into the database?
        ContextWrapper wrapper = new ContextWrapper(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());

        ContactRepository repository = new ContactRepository(messageSenderContext);
        mAllContacts = repository.mAllContactsSimple;

        //Debugging below

        if (mAllContacts != null) {
            Log.d("MessageSender", "mAllContacts: " + mAllContacts.toString());
            Log.d("MessageSender", "contactsListLiveData: " + mAllContacts.toString());
        }
        List<Contact> contactsList = mAllContacts;
        if (contactsList != null) {
            Log.d("MessageSender", "contactsList: " + contactsList.toString());
        } else {
            Log.d("MessageSender", "Error, contactsList null");
        }
    }

    public void sendMessages() {
        // Send the emails to the addresses in the SharedPreferences file.
        Log.i("MessageSender", "Sending emails.");
        List<Contact> contactsList = mAllContacts;
        // Send the emails all selected addresses for each selected contact
        Log.d("MessageSender", "Contacts list size: " + contactsList.size());
        for (Contact contact : contactsList) {
            Log.d("MessageSender", "Contact: " + contact);
            Log.d("MessageSender", "Contact name: " + contact.getContactName());
            Log.d("MessageSender", "Contact Phones: " + contact.getPhoneNumbers().toString());
            Log.d("MessageSender", "Contact Emails: " + contact.getEmailAddresses().toString());
            for (String emailAddress : contact.getEmailAddresses()) {
                Log.d("MessageSender", emailAddress);
                Log.d("MessageSender", "Sending mail to " + emailAddress);
                sendEmail(emailAddress);
            }
        }
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            Log.d("MessageSender", String.valueOf(ste));
        }

        // Send the SMS messages to all selected numbers for each selected contacts
        for (Contact contact : contactsList) {
            for (String phoneNumber : contact.getPhoneNumbers()) {
                Log.d("MessageSender", "Sending SMS to " + phoneNumber);
                sendSMS(phoneNumber);
            }
        }
    }

    private void sendEmail(String address) {
        try {
            new MailSenderTask(messageSenderContext).execute(address);
        } catch (Exception e) {
            Log.e("Message Sender", "Got exception: " + e);
        }
    }

    private void sendSMS(String phoneNumber) {
        String message = sharedPref.getString("pref_message", null);
        SmsManager sms = SmsManager.getDefault();
        if (ContextCompat.checkSelfPermission(messageSenderContext, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sms.sendTextMessage(phoneNumber, null, message, null, null);

        } else {
            ActivityCompat.requestPermissions((Activity) messageSenderContext.getApplicationContext(),
                                              new String[]{Manifest.permission.SEND_SMS},
                                              PERMISSIONS_REQUEST_SEND_SMS);
        }
    }
}
