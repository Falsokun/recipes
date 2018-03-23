package com.hotger.recipes.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hotger.recipes.model.RecipePrev;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RelationDao extends BaseDao<RelationTable> {

    @Query("SELECT * FROM relationtable")
    List<RelationTable> getAllPrevs();

    @Query("SELECT categoryId FROM relationtable WHERE recipeId = :recipeId")
    List<String> getCategoryIdsForRecipe(String recipeId);

    @Query("DELETE FROM relationtable WHERE recipeId = :recipeId")
    void deleteAllWithId(String recipeId);
}
