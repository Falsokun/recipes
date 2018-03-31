package com.hotger.recipes.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hotger.recipes.database.ImageConverter;
import com.hotger.recipes.model.GsonModel.Image;

import java.io.Serializable;

@Entity
public class RecipePrev implements Serializable {

    @NonNull
    @Expose
    @PrimaryKey
    private String id;

    @SerializedName("imageUrlsBySize")
    @Expose
    @TypeConverters({ImageConverter.class})
    private Image images;

    @SerializedName("recipeName")
    @Expose
    private String name;

    @Expose
    private String totalTimeInSeconds;

    public RecipePrev(@NonNull String id, Image images, String name, String totalTimeInSeconds) {
        this.id = id;
        this.images = images;
        this.name = name;
        this.totalTimeInSeconds = totalTimeInSeconds;
    }

    public int getTime() {
        return Integer.parseInt(totalTimeInSeconds) / 60;
    }

    // region Getters and setters
    public String getImageUrl() {
        return images.getUrl();
    }

    public Image getImages() {
        return images;
    }

    public String getName() {
        return name;
    }

    public String getTotalTimeInSeconds() {
        return totalTimeInSeconds;
    }

    public String getId() {
        return id;
    }

    public void setImages(Image images) {
        this.images = images;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalTimeInSeconds(String totalTimeInSeconds) {
        this.totalTimeInSeconds = totalTimeInSeconds;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    //endregion
}