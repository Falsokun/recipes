package com.hotger.recipes.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.hotger.recipes.database.relations.RelationCategoryRecipe;

import java.util.List;

/**
 * Another relation Dao
 */
@Dao
public interface RelationCategoryRecipeDao extends BaseDao<RelationCategoryRecipe> {

    @Query("SELECT * FROM RelationCategoryRecipe")
    List<RelationCategoryRecipe> getAllPrevs();

    @Query("SELECT categoryId FROM RelationCategoryRecipe WHERE recipeId = :recipeId")
    List<String> getCategoryIdsForRecipe(String recipeId);

    @Query("DELETE FROM RelationCategoryRecipe WHERE recipeId = :recipeId")
    void deleteAllWithId(String recipeId);
}
