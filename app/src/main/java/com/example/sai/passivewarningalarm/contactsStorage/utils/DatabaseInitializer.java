package com.example.sai.passivewarningalarm.contactsStorage.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.sai.passivewarningalarm.contactsStorage.AppDatabase;
import com.example.sai.passivewarningalarm.contactsStorage.Contact;

import java.util.ArrayList;
import java.util.List;

public class DatabaseInitializer {
    public static void populateAsync(final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }

    private static void populateWithTestData(AppDatabase db) {
        List<String> phonesTest = new ArrayList<>();
        phonesTest.add("7605937583");
        List<String> emailTest = new ArrayList<>();
        emailTest.add("peregrinebf@gmail.com");
        Contact contact = new Contact("Test", phonesTest, emailTest);

        try {
            db.contactDAO().insert(contact);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }
    }
}
