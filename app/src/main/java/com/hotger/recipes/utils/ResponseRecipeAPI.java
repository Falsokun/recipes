package com.hotger.recipes.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hotger.recipes.model.RecipePrev;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseRecipeAPI implements Serializable {
    @SerializedName("matches")
    @Expose
    ArrayList<RecipePrev> matches;

    public ArrayList<RecipePrev> getMatches() {
        return matches;
    }
}
