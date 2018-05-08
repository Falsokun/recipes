package com.hotger.recipes.model.GsonModel;

import com.google.gson.annotations.Expose;

public class Source {
    @Expose
    private String sourceRecipeUrl;

    public Source() {
        sourceRecipeUrl = "";
    }

    public Source(String sourceRecipeUrl) {
        this.sourceRecipeUrl = sourceRecipeUrl;
    }

    public String getSourceRecipeUrl() {
        return sourceRecipeUrl;
    }

    public void setSourceRecipeUrl(String sourceRecipeUrl) {
        this.sourceRecipeUrl = sourceRecipeUrl;
    }
}
