package com.hotger.recipes.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hotger.recipes.model.RecipePrev;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RelationDao {

    @Insert(onConflict = REPLACE)
    void insertAll(List<RelationTable> prevs);

    @Delete
    void delete(RecipePrev prev);

    @Query("SELECT * FROM relationtable")
    List<RelationTable> getAllPrevs();

    @Insert(onConflict = REPLACE)
    void insert(RelationTable prev);

    @Query("SELECT categoryId FROM relationtable WHERE recipeId = :recipeId")
    List<String> getCategoryIdsForRecipe(String recipeId);
}
