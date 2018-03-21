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
public interface RecipeDao {

    @Insert(onConflict = REPLACE)
    void insert(RecipeNF recipe);

    @Delete
    void delete(RecipeNF recipe);

    @Transaction @Query("SELECT * FROM recipenf")
    List<Recipe> getAllRecipes();

    @Transaction @Query("SELECT * FROM recipenf WHERE id = :dialogId")
    List<Recipe> getRecipesById(String dialogId);

//    @Transaction @Query("SELECT * FROM recipenf WHERE total LIKE :time")
//    List<Recipe> getAllRecipesWithTimeLess(String time);

}
