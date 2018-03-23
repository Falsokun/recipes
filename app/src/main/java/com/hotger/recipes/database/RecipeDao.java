package com.hotger.recipes.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.hotger.recipes.model.RecipeNF;
import com.hotger.recipes.model.Recipe;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipeDao extends BaseDao<RecipeNF> {

    @Transaction @Query("SELECT * FROM recipenf WHERE id = :dialogId")
    List<Recipe> getRecipesById(String dialogId);

    @Query("DELETE FROM recipenf WHERE id = :recipeId")
    void deleteById(String recipeId);
}
