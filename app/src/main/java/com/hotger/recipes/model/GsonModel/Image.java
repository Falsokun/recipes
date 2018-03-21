package com.hotger.recipes.model.GsonModel;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

//@Entity
public class Image implements Serializable {
//    @PrimaryKey
//    @NonNull
@SerializedName(value="hostedLargeUrl", alternate={"90"})
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

    public String toString() {
        return url;
    }

    public static Image fromString(String string) {
        return new Image(string.split(";")[0]);
    }
}