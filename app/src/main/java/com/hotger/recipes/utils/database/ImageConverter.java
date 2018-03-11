package com.hotger.recipes.utils.database;

import android.arch.persistence.room.TypeConverter;

import com.hotger.recipes.utils.model.Image;

public class ImageConverter {
    @TypeConverter
    public String fromImage(Image image) {
        return image.getUrl();
    }

    @TypeConverter
    public Image toImage(String data) {
        return new Image(data);
    }
}
