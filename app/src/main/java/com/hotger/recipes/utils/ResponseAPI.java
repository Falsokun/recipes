package com.hotger.recipes.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hotger.recipes.model.RecipePrev;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Dummy class for getting response of recipePrev's
 */
public class ResponseAPI<T> implements Serializable {
    @SerializedName("matches")
    @Expose
    ArrayList<T> results;

    public ArrayList<T> getMatches() {
        return results;
    }
}
