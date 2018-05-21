package com.hotger.recipes.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.hotger.recipes.model.ApiRecipe;
import com.hotger.recipes.model.Recipe;

import java.util.List;

@Dao
public interface RecipeDao extends BaseDao<ApiRecipe> {

    @Transaction @Query("SELECT * FROM ApiRecipe WHERE id = :recipeId")
    List<Recipe> getRecipesById(String recipeId);

    @Query("DELETE FROM ApiRecipe WHERE id = :recipeId")
    void deleteById(String recipeId);

    @Transaction @Query("SELECT * FROM ApiRecipe")
    List<Recipe> getAll();
}
