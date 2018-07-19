package com.github.carlhmitchell.failsafealert.utilities;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.github.carlhmitchell.contactablespicker.Storage.Contact;
import com.github.carlhmitchell.contactablespicker.Storage.ContactRepository;
import com.github.carlhmitchell.failsafealert.email.MailSenderTask;
import com.intentfilter.androidpermissions.PermissionManager;

import java.util.List;

import static java.util.Collections.singleton;


public class MessageSender {
    private final Context messageSenderContext;
    private final SharedPreferences sharedPref;
    private final List<Contact> mAllContacts;

    public MessageSender(Context context) {
        messageSenderContext = context;


        // Shared Pref to get the message.
        // TODO: Move into the database?
        ContextWrapper wrapper = new ContextWrapper(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());

        ContactRepository repository = new ContactRepository(messageSenderContext);
        // We need to instantiate the repository before using it, to ensure its static members
        //     get initialized.
        // TODO: change this in the library so a getter can be used.
        //noinspection AccessStaticViaInstance
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
        /*
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            Log.d("MessageSender", String.valueOf(ste));
        }
        */

        // Send the SMS messages to all selected numbers for each selected contacts
        for (Contact contact : contactsList) {
            for (String phoneNumber : contact.getPhoneNumbers()) {
                if (!phoneNumber.equals("")) {
                    Log.d("MessageSender", "Sending SMS to " + phoneNumber);
                    sendSMS(phoneNumber);
                }
            }
        }
    }

    private void sendEmail(String address) {
        try {
            MailSenderTask task = new MailSenderTask(messageSenderContext);
            task.execute(address);
        } catch (Exception e) {
            Log.e("Message Sender", "Got exception: " + e);
        }
    }

    private void sendSMS(final String phoneNumber) {
        final String message = sharedPref.getString("pref_message", null);
        final SmsManager sms = SmsManager.getDefault();
        PermissionManager permissionManager = PermissionManager.getInstance(messageSenderContext);
        permissionManager.checkPermissions(singleton(Manifest.permission.SEND_SMS), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(messageSenderContext, "Permissions Granted", Toast.LENGTH_SHORT).show();
                sms.sendTextMessage(phoneNumber, null, message, null, null);
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(messageSenderContext, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
