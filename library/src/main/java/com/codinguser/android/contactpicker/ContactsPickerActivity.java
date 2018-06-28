/*
 * Copyright (C) 2011 - 2015 by Ngewi Fet <ngewif@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.codinguser.android.contactpicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class ContactsPickerActivity extends AppCompatActivity implements OnContactSelectedListener {
    public static final String ARG_CONTACT_ID = "contact_id";
    public static final String ARG_PICKER_MODE = "request_code";

    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_CONTACT_NAME = "contact_name";
    public static final String KEY_CONTACT_EMAIL = "email";
    /**
     * Request codes depending on what the user should select
     */
    public static final int REQUEST_CONTACT_PHONE = 0x10;
    public static final int REQUEST_CONTACT_EMAIL = 0x11;
    protected int mRequestCode = REQUEST_CONTACT_PHONE;

    /**
     * Starting point
     * Loads the {@link ContactsListFragment}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mRequestCode = getIntent().getIntExtra(ARG_PICKER_MODE, /* default value: */REQUEST_CONTACT_PHONE);

        PickerMode pickerMode;
        switch (mRequestCode) {
            case REQUEST_CONTACT_EMAIL:
                pickerMode = PickerMode.EMAIL;
                break;

            case REQUEST_CONTACT_PHONE:
                pickerMode = PickerMode.PHONE;
                break;

            default:
                // TODO: add user warning
                pickerMode = PickerMode.PHONE;
                break;
        }

        ContactsListFragment fragment = ContactsListFragment.newInstance(pickerMode);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Select contact");
        }
    }

    /**
     * Callback when the contact is selected from the list of contacts.
     * Loads the {@link ContactDetailsFragment}
     */
    @Override
    public void onContactNameSelected(long contactId) {
        /* Now that we know which Contact was selected we can go to the details fragment */

        Fragment detailsFragment = new ContactDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CONTACT_ID, contactId);
        args.putInt(ARG_PICKER_MODE, mRequestCode);
        detailsFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, detailsFragment);

        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    /**
     * Callback when the contact number is selected from the contact details view
     * Sets the activity result with the contact information and finishes
     */
    @Override
    public void onContactNumberSelected(String contactNumber, String contactName) {
        Intent intent = new Intent();
        intent.putExtra(KEY_PHONE_NUMBER, contactNumber);
        intent.putExtra(KEY_CONTACT_NAME, contactName);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onContactEmailSelected(String emailAddress, String contactName) {
        Intent intent = new Intent();
        intent.putExtra(KEY_CONTACT_EMAIL, emailAddress);
        intent.putExtra(KEY_CONTACT_NAME, contactName);

        setResult(RESULT_OK, intent);
        finish();
    }

    public enum PickerMode {PHONE, EMAIL}
}