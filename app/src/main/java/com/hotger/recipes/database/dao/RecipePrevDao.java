package com.hotger.recipes.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.hotger.recipes.model.RecipePrev;

import java.util.List;

/**
 * DAO for recipe previews
 * Fields: id, images, recipeName, totalTimeInSeconds
 * (вообще говоря лучше было бы Prev и RecipeDAO объединить, по реляционным соображениям, но исходя
 *  из того, что это все подкачивается из другой апишки другим запросом и в целом это не нужно,
 *  решила не делать)
 */
@Dao
public interface RecipePrevDao extends BaseDao<RecipePrev> {

    @Query("DELETE FROM recipeprev WHERE id = :recipeId")
    void deleteAllById(String recipeId);

    @Query("SELECT * FROM recipeprev WHERE id IN(:recipeListId)")
    List<RecipePrev> findPrevsFromList(List<String> recipeListId);

    @Transaction @Query("SELECT * FROM recipeprev")
    List<RecipePrev> getAll();

    @Transaction @Query("SELECT * FROM recipeprev WHERE id = :recipeId")
    List<RecipePrev> find(String recipeId);
}
