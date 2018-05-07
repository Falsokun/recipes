package com.hotger.recipes.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hotger.recipes.database.ObjConverter;
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
    @TypeConverters({ObjConverter.class})
    private Image images;

    @SerializedName("recipeName")
    @Expose
    private String name;

    @Expose
    private int totalTimeInSeconds;

    private boolean isFromYummly = true;

    @Ignore
    public RecipePrev() {
    }

    @Ignore
    public RecipePrev(@NonNull String id, Image images, String name, int totalTimeInSeconds) {
        this.id = id;
        this.images = images;
        this.name = name;
        this.totalTimeInSeconds = totalTimeInSeconds;
    }

    public RecipePrev(@NonNull String id, Image images, String name, int totalTimeInSeconds, boolean isFromYummly) {
        this.id = id;
        this.images = images;
        this.name = name;
        this.totalTimeInSeconds = totalTimeInSeconds;
        this.isFromYummly = isFromYummly;
    }

    @Exclude
    public int getTime() {
        return totalTimeInSeconds / 60;
    }

    // region Getters and setters
    @Exclude
    public String getImageUrl() {
        return images.getUrl();
    }

    public Image getImages() {
        return images;
    }

    public String getName() {
        return name;
    }

    public int getTotalTimeInSeconds() {
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

    public void setTotalTimeInSeconds(int totalTimeInSeconds) {
        this.totalTimeInSeconds = totalTimeInSeconds;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public boolean isFromYummly() {
        return isFromYummly;
    }

    public void setFromYummly(boolean fromYummly) {
        isFromYummly = fromYummly;
    }

    //endregion
}