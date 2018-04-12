package com.hotger.recipes.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.hotger.recipes.model.RecipePrev;

import java.util.List;

@Dao
public interface RecipePrevDao extends BaseDao<RecipePrev> {

    @Query("DELETE FROM recipeprev WHERE id = :recipeId")
    void deleteAllById(String recipeId);

    @Query("SELECT * FROM recipeprev WHERE id IN(:recipeListId)")
    List<RecipePrev> findPrevsFromList(List<String> recipeListId);

    @Transaction @Query("SELECT * FROM recipeprev")
    List<RecipePrev> getAll();
}
