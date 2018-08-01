package com.github.carlhmitchell.failsafealert.utilities;

//Model?

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.github.carlhmitchell.contactablespicker.Storage.Contact;
import com.github.carlhmitchell.contactablespicker.Storage.ContactRepository;
import com.github.carlhmitchell.failsafealert.R;
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
        ContextWrapper wrapper = new ContextWrapper(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(wrapper.getBaseContext());

        ContactRepository repository = new ContactRepository(messageSenderContext);
        mAllContacts = repository.getmAllContactsSimple();

        //Debugging below

        if (mAllContacts != null) {
            SDLog.d("MessageSender", "mAllContacts: " + mAllContacts.toString());
            SDLog.d("MessageSender", "contactsListLiveData: " + mAllContacts.toString());
        }
        List<Contact> contactsList = mAllContacts;
        if (contactsList != null) {
            SDLog.d("MessageSender", "contactsList: " + contactsList.toString());
        } else {
            SDLog.d("MessageSender", "Error, contactsList null");
        }
    }

    public void sendHelpRequest(boolean isTest) {
        // Send the emails to the addresses in the SharedPreferences file.
        SDLog.i("MessageSender", "Sending messages.");
        List<Contact> contactsList = mAllContacts;
        // Send the emails all selected addresses for each selected contact
        SDLog.d("MessageSender", "Contacts list size: " + contactsList.size());
        for (Contact contact : contactsList) {
            SDLog.d("MessageSender", "Contact: " + contact);
            SDLog.d("MessageSender", "Contact name: " + contact.getContactName());
            SDLog.d("MessageSender", "Contact Phones: " + contact.getPhoneNumbers().toString());
            SDLog.d("MessageSender", "Contact Emails: " + contact.getEmailAddresses().toString());
            for (String emailAddress : contact.getEmailAddresses()) {
                SDLog.d("MessageSender", emailAddress);
                SDLog.d("MessageSender", "Sending mail to " + emailAddress);
                sendEmail(emailAddress, isTest);
            }
        }

        // Send the SMS messages to all selected numbers for each selected contacts
        for (Contact contact : contactsList) {
            for (String phoneNumber : contact.getPhoneNumbers()) {
                if (!phoneNumber.equals("")) {
                    SDLog.d("MessageSender", "Sending SMS to " + phoneNumber);
                    sendSMS(phoneNumber, isTest);
                }
            }
        }
    }

    private void sendEmail(String address, boolean isTest) {
        try {
            MailSenderTask task = new MailSenderTask(messageSenderContext);
            if (isTest) {
                task.execute(address, "true");
            } else {
                task.execute(address, "false");
            }
            ToastHelper.toast(messageSenderContext, "Email sent", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            SDLog.e("Message Sender", "Got exception: " + e);
            ToastHelper.toast(messageSenderContext, "Email failed to send", Toast.LENGTH_LONG);
            NotificationHelper helper = new NotificationHelper(messageSenderContext);
            helper.sendNotification(messageSenderContext.getString(R.string.email_send_error_notification_title),
                                    messageSenderContext.getString(R.string.email_send_error_notification_text));
        }
    }

    private void sendSMS(final String phoneNumber, boolean isTest) {
        final String message;
        if (isTest) {
            message = messageSenderContext.getString(R.string.test_message);
        } else {
            message = sharedPref.getString("pref_message", null);
        }
        final SmsManager sms = SmsManager.getDefault();
        PermissionManager permissionManager = PermissionManager.getInstance(messageSenderContext);
        permissionManager.checkPermissions(singleton(Manifest.permission.SEND_SMS), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                ToastHelper.toast(messageSenderContext, "Permissions Granted", Toast.LENGTH_SHORT);
                sms.sendTextMessage(phoneNumber, null, message, null, null);
            }

            @Override
            public void onPermissionDenied() {
                ToastHelper.toast(messageSenderContext, "Permissions Denied", Toast.LENGTH_SHORT);
            }
        });
    }
}
