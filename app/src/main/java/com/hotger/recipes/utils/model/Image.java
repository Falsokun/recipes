package com.hotger.recipes.utils.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class Image implements Serializable {
    @PrimaryKey
    @NonNull
    @SerializedName("90")
    @Expose
    private String url;

    public Image(String url) {
        this.url = url;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }
}