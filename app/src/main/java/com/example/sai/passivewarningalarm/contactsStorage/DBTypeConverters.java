package com.example.sai.passivewarningalarm.contactsStorage;

import android.arch.persistence.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class DBTypeConverters {
    @TypeConverter
    public static String listToString(List<String> value) {
        return value.toString();
    }

    @TypeConverter
    public static List<String> stringToList(String values) {
        String stripped = values.substring(1, values.length() - 1);
        String[] array = stripped.split(",");
        return Arrays.asList(array);
    }
}
