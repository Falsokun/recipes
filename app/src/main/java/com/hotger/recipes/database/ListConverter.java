package com.hotger.recipes.database;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import com.hotger.recipes.model.GsonModel.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListConverter {
    @TypeConverter
    public String fromList(List<Image> urls) {
        List<String> array = new ArrayList<>();
        for(Image url : urls) {
            array.add(url.toString());
        }

        return TextUtils.join(", ", array);
    }

    @TypeConverter
    public List<Image> fromListURL(String data) {
        List<Image> url = new ArrayList<>();
        for (String s : Arrays.asList(data.split(","))) {
            url.add(Image.fromString(s));
        }

        return url;
    }
}
