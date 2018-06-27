package com.example.sai.passivewarningalarm.contactsStorage;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

@Entity(tableName = "contacts_table")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "contact_name")
    private String contactName;

    @ColumnInfo(name = "phone_numbers")
    private List<String> phoneNumbers;

    @ColumnInfo(name = "email_addresses")
    private List<String> emailAddresses;

    @Ignore
    public Contact(int id, String contactName, List<String> phoneNumbers, List<String> emailAddresses) {
        this.id = id;
        this.contactName = contactName;
        this.phoneNumbers = phoneNumbers;
        this.emailAddresses = emailAddresses;
    }

    public Contact(String contactName, List<String> phoneNumbers, List<String> emailAddresses) {
        //newId();
        this.contactName = contactName;
        this.phoneNumbers = phoneNumbers;
        this.emailAddresses = emailAddresses;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public String toString() {
        //Ensure there's data in both phoneNumbers and emailAddresses. If not, set to empty lists.
        if (phoneNumbers != null && emailAddresses != null) {
            return "" + id + "," + contactName + "," + phoneNumbers.toString() + "," + emailAddresses.toString();
        } else if (phoneNumbers == null) {
            return "" + id + "," + contactName + "," + "[\"\"]" + "," + emailAddresses.toString();
        } else {
            return "" + id + "," + contactName + "," + emailAddresses.toString() + "," + "[\"\"]";
        }
    }

    public Contact getContact() {
        return this;
    }

    /*
    public void newId() {
        //this.id = java.util.UUID.randomUUID();
    }
    */

}
