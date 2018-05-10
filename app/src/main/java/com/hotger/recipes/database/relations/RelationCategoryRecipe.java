package com.hotger.recipes.database.relations;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

/**
 * Relation to category and recipe
 */
@Entity(primaryKeys = {"recipeId", "categoryId"})
public class RelationCategoryRecipe {
    @NonNull
    private String recipeId;

    @NonNull
    private String categoryId;

    @Ignore
    public RelationCategoryRecipe() {}

    public RelationCategoryRecipe(@NonNull String recipeId, @NonNull String categoryId) {
        this.recipeId = recipeId;
        this.categoryId = categoryId;
    }

    //region getters and setters
    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(@NonNull String recipeId) {
        this.recipeId = recipeId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@NonNull String categoryId) {
        this.categoryId = categoryId;
    }
    //endregion
}
