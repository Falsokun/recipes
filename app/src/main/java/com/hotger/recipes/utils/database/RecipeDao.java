package com.hotger.recipes.utils.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hotger.recipes.utils.model.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {

    @Insert
    void insertAll(Recipe... recipes);

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * FROM recipe")
    List<Recipe> getAllPeople();

    @Query("SELECT * FROM person WHERE totalTimeInSeconds LIKE :time")
    List<Recipe> getAllRecipesWithTimeLess(String time);

}
