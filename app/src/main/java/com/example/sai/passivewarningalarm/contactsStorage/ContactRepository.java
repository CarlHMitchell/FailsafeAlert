package com.example.sai.passivewarningalarm.contactsStorage;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class ContactRepository {
    private ContactDAO mContactDAO;
    private List<Contact> mAllContacts;
    private LiveData<List<Contact>> mAllContactsLD;

    public ContactRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context.getApplicationContext());
        mContactDAO = db.contactDAO();
        mAllContacts = mContactDAO.getAll();
        mAllContactsLD = mContactDAO.getAllLD();
    }


    public LiveData<List<Contact>> getAllContactsLD() {

        return mAllContactsLD;
    }


    public List<Contact> getAllContacts() {
        return mAllContacts;
    }

    public int getCount() {
        return mContactDAO.getCount();
    }

    public void insert(Contact contact) {
        new insertAsyncTask(mContactDAO).execute(contact);
    }

    public void delete(Contact contact) {
        new deleteAsyncTask(mContactDAO).execute(contact);
    }

    private static class insertAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDAO mAsyncTaskDao;

        insertAsyncTask(ContactDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Contact... params) {
            mAsyncTaskDao.insert(params[0]);
            Log.d("ContactRepository", "Inserted contact: " + params[0].toString());
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDAO mAsyncTaskDao;

        deleteAsyncTask(ContactDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Contact... params) {
            mAsyncTaskDao.delete(params[0]);
            Log.d("ContactRepository", "Deleted contact: " + params[0].toString());
            return null;
        }
    }
}
