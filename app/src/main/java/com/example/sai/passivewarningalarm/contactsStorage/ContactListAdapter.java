package com.example.sai.passivewarningalarm.contactsStorage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sai.passivewarningalarm.R;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    private final LayoutInflater mInflater;
    private List<Contact> mContacts; // Cached copy of Contacts

    ContactListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    @NonNull
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        if (mContacts != null) {
            Contact current = mContacts.get(position);
            holder.contactItemView.setText(current.getContact().toString());
        } else {
            // Covers the case of data not being ready yet.
            holder.contactItemView.setText(R.string.no_contacts_to_display);
        }
    }

    void setContacts(List<Contact> contacts) {
        mContacts = contacts;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mContacts has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mContacts != null) {
            return mContacts.size();
        } else {
            return 0;
        }
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView contactItemView;

        private ContactViewHolder(View itemView) {
            super(itemView);
            contactItemView = itemView.findViewById(R.id.recyclerTextView);
        }
    }
}
