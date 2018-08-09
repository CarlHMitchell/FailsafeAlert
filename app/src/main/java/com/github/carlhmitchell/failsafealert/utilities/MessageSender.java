package com.github.carlhmitchell.failsafealert.utilities;

//Model?

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.github.carlhmitchell.contactablespicker.Storage.Contact;
import com.github.carlhmitchell.contactablespicker.Storage.ContactRepository;
import com.github.carlhmitchell.failsafealert.R;
import com.github.carlhmitchell.failsafealert.email.MailSenderTask;
import com.github.carlhmitchell.failsafealert.utilities.background.AlarmReceiver;

import java.util.List;
import java.util.Objects;

import static android.app.PendingIntent.getBroadcast;
import static com.github.carlhmitchell.failsafealert.Messaging.SMSSender.sendSMS;
import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.ACTION_ALERT;


public class MessageSender {
    private final Context messageSenderContext;
    private final List<Contact> mAllContacts;
    private final String DEBUG_TAG = MessageSender.class.getSimpleName();

    public MessageSender(Context context) {
        messageSenderContext = context;

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

    public boolean sendHelpRequest(boolean isTest) {
        SDLog.i("MessageSender", "Sending messages.");
        List<Contact> contactsList = mAllContacts;
        if (isTest) {
            if (contactsList.size() == 0) {
                ToastHelper.toast(messageSenderContext, messageSenderContext.getString(R.string.empty_contacts_list), Toast.LENGTH_SHORT);
            } else {
                ToastHelper.toast(messageSenderContext, messageSenderContext.getString(R.string.test_sent_toast), Toast.LENGTH_SHORT);
            }
        }

        // Send the emails all selected addresses for each selected contact
        SDLog.d("MessageSender", "Contacts list size: " + contactsList.size());
        for (Contact contact : contactsList) {
            SDLog.d("MessageSender", "Contact: " + contact);
            SDLog.d("MessageSender", "Contact name: " + contact.getContactName());
            SDLog.d("MessageSender", "Contact Phones: " + contact.getPhoneNumbers().toString());
            SDLog.d("MessageSender", "Contact Emails: " + contact.getEmailAddresses().toString());
            for (String emailAddress : contact.getEmailAddresses()) {
                if (!emailAddress.equals("")) {
                    SDLog.d("MessageSender", emailAddress);
                    SDLog.d("MessageSender", "Sending mail to " + emailAddress);
                    if (!sendEmail(emailAddress, MessageBuilder.buildMessage(messageSenderContext, isTest))) {
                        //Email send failed.
                        if (!isTest) {
                            //Sending failed, not a test.
                            // User presumed incapacitated. Keep sending every 15 minutes until success.
                            // This will spam other users in the contacts list, but it's better safe than sorry.
                            Intent alertIntent = new Intent(messageSenderContext, AlarmReceiver.class);
                            alertIntent.setAction(ACTION_ALERT);
                            // Create a PendingIntent to be triggered when the alarm goes off
                            // The PendingIntent.FLAG_UPDATE_CURRENT means that if the alarm fires quickly the events replace each other rather than stack up.
                            PendingIntent alertPendingIntent = getBroadcast(messageSenderContext, AlarmReceiver.ALERT, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarm = (AlarmManager) messageSenderContext.getSystemService(Context.ALARM_SERVICE);
                            Objects.requireNonNull(alarm).set(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, alertPendingIntent);
                            SDLog.e(DEBUG_TAG, "Error sending email. Alarm scheduled for 15 minutes from now.");
                            return false;
                        }
                    }
                }
            }
        }

        // Send the SMS messages to all selected numbers for each selected contacts
        for (Contact contact : contactsList) {
            for (String phoneNumber : contact.getPhoneNumbers()) {
                if (!phoneNumber.equals("")) {
                    SDLog.d("MessageSender", "Sending SMS to " + phoneNumber);
                    sendSMS(messageSenderContext, phoneNumber, MessageBuilder.buildMessage(messageSenderContext, isTest));
                    // No easy way to tell if this worked.
                    //TODO: Figure out how to recieve broadcasts from SMS sending.
                }
            }
        }
        return true;
    }

    private boolean sendEmail(String address, String message) {
        try {
            MailSenderTask task = new MailSenderTask(messageSenderContext);
                task.execute(address, message);
            ToastHelper.toast(messageSenderContext, "Email sent", Toast.LENGTH_SHORT);
            return true;
        } catch (Exception e) {
            SDLog.e("Message Sender", "Got exception: " + e);
            ToastHelper.toast(messageSenderContext, "Email failed to send", Toast.LENGTH_LONG);
            NotificationHelper helper = new NotificationHelper(messageSenderContext);
            helper.sendNotification(messageSenderContext.getString(R.string.email_send_error_notification_title),
                                    messageSenderContext.getString(R.string.email_send_error_notification_text) + "\n" + e);
            return false;
        }
    }

}
