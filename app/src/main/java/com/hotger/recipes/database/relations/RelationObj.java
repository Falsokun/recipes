package com.hotger.recipes.database.relations;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class RelationObj {
    @PrimaryKey
    @NonNull
    String recipeId;

    public RelationObj(String recipeId) {
        this.recipeId = recipeId;
    }

    //region Getters and setters
    @NonNull
    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(@NonNull String recipeId) {
        this.recipeId = recipeId;
    }
    //endregion
}
