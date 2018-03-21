package com.hotger.recipes.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class RelationTable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public RelationTable(String recipeId, String categoryId) {
        this.recipeId = recipeId;
        this.categoryId = categoryId;
    }

    private String recipeId;

    private String categoryId;

    //region getters and setters
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    //endregion
}
