package com.hotger.recipes.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.hotger.recipes.database.RelationObj;

import java.util.List;

@Dao
public interface FavoritesDao extends BaseDao<RelationObj> {
    @Query("SELECT * from RelationObj")
    LiveData<List<RelationObj>> getFavorites();

    @Query("SELECT recipeId from RelationObj")
    LiveData<List<String>> getStringFavorites();
}
