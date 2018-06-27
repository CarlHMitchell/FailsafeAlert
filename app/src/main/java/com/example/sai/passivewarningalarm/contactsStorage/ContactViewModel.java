package com.example.sai.passivewarningalarm.contactsStorage;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    private ContactRepository mRepository;
    private List<Contact> mAllContacts;
    private LiveData<List<Contact>> mAllContactsLD;

    public ContactViewModel(Application application) {
        super(application);
        mRepository = new ContactRepository(application.getApplicationContext());
        mAllContacts = mRepository.getAllContacts();
        mAllContactsLD = mRepository.getAllContactsLD();
    }


    LiveData<List<Contact>> getAllContactsLD() {
        return mAllContactsLD;
    }

    List<Contact> getAllContacts() {
        return mAllContacts;
    }

    public void insert(Contact contact) {
        mRepository.insert(contact);
    }

    public void delete(Contact contact) {
        mRepository.delete(contact);
    }
}
