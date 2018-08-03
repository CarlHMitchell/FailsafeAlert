package com.github.carlhmitchell.failsafealert.utilities;

//Model?

import android.content.Context;
import android.widget.Toast;

import com.github.carlhmitchell.contactablespicker.Storage.Contact;
import com.github.carlhmitchell.contactablespicker.Storage.ContactRepository;
import com.github.carlhmitchell.failsafealert.R;
import com.github.carlhmitchell.failsafealert.email.MailSenderTask;

import java.util.List;

import static com.github.carlhmitchell.failsafealert.Messaging.SMSSender.sendSMS;


public class MessageSender {
    private final Context messageSenderContext;
    private final List<Contact> mAllContacts;

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

    public void sendHelpRequest(boolean isTest) {
        SDLog.i("MessageSender", "Sending messages.");
        List<Contact> contactsList = mAllContacts;
        if (isTest) {
            if (contactsList.size() == 0) {
                ToastHelper.toast(messageSenderContext, "Your contacts list is empty!", Toast.LENGTH_SHORT);
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
                    sendEmail(emailAddress, MessageBuilder.buildMessage(messageSenderContext, isTest));
                }
            }
        }

        // Send the SMS messages to all selected numbers for each selected contacts
        for (Contact contact : contactsList) {
            for (String phoneNumber : contact.getPhoneNumbers()) {
                if (!phoneNumber.equals("")) {
                    SDLog.d("MessageSender", "Sending SMS to " + phoneNumber);
                    sendSMS(messageSenderContext, phoneNumber, MessageBuilder.buildMessage(messageSenderContext, isTest));
                }
            }
        }
    }

    private void sendEmail(String address, String message) {
        try {
            MailSenderTask task = new MailSenderTask(messageSenderContext);
                task.execute(address, message);
            ToastHelper.toast(messageSenderContext, "Email sent", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            SDLog.e("Message Sender", "Got exception: " + e);
            ToastHelper.toast(messageSenderContext, "Email failed to send", Toast.LENGTH_LONG);
            NotificationHelper helper = new NotificationHelper(messageSenderContext);
            helper.sendNotification(messageSenderContext.getString(R.string.email_send_error_notification_title),
                                    messageSenderContext.getString(R.string.email_send_error_notification_text) + "\n" + e);
        }
    }

}
