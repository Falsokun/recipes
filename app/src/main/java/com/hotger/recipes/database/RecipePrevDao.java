package com.hotger.recipes.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hotger.recipes.model.RecipePrev;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipePrevDao {

    @Insert(onConflict = REPLACE)
    void insertAll(List<RecipePrev> prevs);

    @Delete
    void delete(RecipePrev prev);

    @Query("SELECT * FROM recipeprev")
    List<RecipePrev> getAllPrevs();

    @Query("SELECT * FROM recipeprev WHERE type = :type")
    List<RecipePrev> getRecipesByType(String type);

    @Insert(onConflict = REPLACE)
    void insert(RecipePrev prev);
}
