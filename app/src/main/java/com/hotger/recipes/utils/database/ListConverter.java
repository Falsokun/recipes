package com.hotger.recipes.utils.database;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

public class ListConverter {
    @TypeConverter
    public String fromList(List<String> hobbies) {
        return TextUtils.join(", ", hobbies);
    }

    @TypeConverter
    public List<String> toList(String data) {
        return Arrays.asList(data.split(","));
    }
}
