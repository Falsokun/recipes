package com.hotger.recipes.model.GsonModel;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Class to get response from {@link com.hotger.recipes.utils.YummlyAPI} by gson
 */
public class Image implements Serializable {
@SerializedName(value="hostedLargeUrl", alternate={"90"})
@Expose
    private String url;

    public Image() {}

    public Image(String url) {
        this.url = url;
    }

    //region Setters and getters
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
    //endregion
}