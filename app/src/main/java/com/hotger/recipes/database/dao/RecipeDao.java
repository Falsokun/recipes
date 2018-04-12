package com.hotger.recipes.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.hotger.recipes.model.RecipeNF;
import com.hotger.recipes.model.Recipe;

import java.util.List;

@Dao
public interface RecipeDao extends BaseDao<RecipeNF> {

    @Transaction @Query("SELECT * FROM recipenf WHERE id = :dialogId")
    List<Recipe> getRecipesById(String dialogId);

    @Query("DELETE FROM recipenf WHERE id = :recipeId")
    void deleteById(String recipeId);

    @Transaction @Query("SELECT * FROM recipenf")
    List<Recipe> getAll();
}
