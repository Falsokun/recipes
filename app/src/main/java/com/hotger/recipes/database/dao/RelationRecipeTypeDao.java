package com.hotger.recipes.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.hotger.recipes.database.relations.RelationRecipeType;

import java.util.List;

@Dao
public interface RelationRecipeTypeDao extends BaseDao<RelationRecipeType> {

    @Query("SELECT recipeId FROM relationrecipetype WHERE type = :searchValue")
    List<String> getRecipesByType(String searchValue);

    @Query("SELECT recipeId FROM relationrecipetype WHERE type = :searchValue")
    LiveData<List<String>> getLiveRecipesByType(String searchValue);

    @Query("SELECT recipeId FROM relationrecipetype WHERE type = :type")
    LiveData<List<String>> getFavorites(String type);

    @Query("SELECT * FROM relationrecipetype WHERE recipeId = :id and type = :typeMyFavs")
    List<RelationRecipeType> getRelation(String id, String typeMyFavs);

    @Query("SELECT recipeId FROM relationrecipetype WHERE type = :type and recipeId = :id")
    List<String> getRecipesById(String id, String type);

    @Query("DELETE FROM relationrecipetype WHERE recipeId = :id")
    void deleteWhereId(String id);
}
