package com.hotger.recipes.model.GsonModel;

import com.google.gson.annotations.Expose;

/**
 * Class to get response from {@link com.hotger.recipes.utils.YummlyAPI} by gson
 */
public class Source {
    @Expose
    private String sourceRecipeUrl;

    public Source() {
        sourceRecipeUrl = "";
    }

    public Source(String sourceRecipeUrl) {
        this.sourceRecipeUrl = sourceRecipeUrl;
    }

    //region Setters and getters
    public String getSourceRecipeUrl() {
        return sourceRecipeUrl;
    }

    public void setSourceRecipeUrl(String sourceRecipeUrl) {
        this.sourceRecipeUrl = sourceRecipeUrl;
    }
    //endregion
}
