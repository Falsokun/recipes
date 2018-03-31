package com.hotger.recipes.database;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"recipeId", "type"})
public class RelationRecipeType {

    @NonNull
    String recipeId;

    @NonNull
    String type;

    public RelationRecipeType(@NonNull String recipeId, @NonNull String type) {
        this.recipeId = recipeId;
        this.type = type;
    }

    @NonNull
    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(@NonNull String recipeId) {
        this.recipeId = recipeId;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }
}
