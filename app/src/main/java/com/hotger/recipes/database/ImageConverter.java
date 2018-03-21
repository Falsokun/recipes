package com.hotger.recipes.database;

import android.arch.persistence.room.TypeConverter;

import com.hotger.recipes.model.GsonModel.Image;

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
