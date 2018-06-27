package com.example.sai.passivewarningalarm.contactsStorage;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ContactDAO {
    @Query("SELECT * FROM contacts_table")
    List<Contact> getAll();

    @Query("SELECT * FROM contacts_table")
    LiveData<List<Contact>> getAllLD();

    @Query("SELECT * FROM contacts_table WHERE id IN(:ids)")
    LiveData<List<Contact>> loadAllByIdsLD(int[] ids);

    @Query("SELECT * FROM contacts_table WHERE id IN(:ids)")
    List<Contact> loadAllByIds(int[] ids);

    @Query("SELECT * FROM contacts_table WHERE contact_name LIKE :displayName LIMIT 1")
    LiveData<Contact> findByNameLD(String displayName);

    @Query("SELECT * FROM contacts_table WHERE contact_name LIKE :displayName LIMIT 1")
    Contact findByName(String displayName);

    @Query("SELECT COUNT (*) FROM contacts_table")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Contact contact);

    @Delete
    void delete(Contact contact);

    @Query("DELETE FROM contacts_table")
    void deleteAll();


}
