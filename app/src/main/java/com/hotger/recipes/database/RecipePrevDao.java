package com.hotger.recipes.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hotger.recipes.model.RecipePrev;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipePrevDao extends BaseDao<RecipePrev> {

    @Query("SELECT * FROM recipeprev WHERE type = :type")
    LiveData<List<RecipePrev>> getRecipesByType(String type);


    @Query("DELETE FROM recipeprev WHERE id = :recipeId")
    void deleteAllById(String recipeId);
}
