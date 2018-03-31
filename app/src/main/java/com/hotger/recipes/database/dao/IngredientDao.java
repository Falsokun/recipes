package com.hotger.recipes.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.hotger.recipes.model.Ingredient;

import java.util.List;

@Dao
public interface IngredientDao extends BaseDao<Ingredient> {
    @Query("SELECT * FROM ingredient")
    List<Ingredient> getAllIngredients();

    @Query("SELECT * FROM ingredient WHERE ru LIKE :russian")
    List<Ingredient> getEnTranslation(String russian);

    @Query("SELECT * FROM ingredient WHERE en LIKE :english")
    List<Ingredient> getRuTranslation(String english);

    @Query("SELECT en FROM ingredient")
    List<String> getEnglishNames();

    @Query("SELECT ru FROM ingredient")
    List<String> getRussianNames();

    @Query("SELECT measure FROM ingredient WHERE en like :english")
    String getMeasureOfProduct(String english);

    @Query("SELECT * FROM ingredient WHERE en = :name OR ru = :name")
    List<Ingredient> getIngredientByName(String name);

    @Query("SELECT * FROM ingredient WHERE id = :id")
    List<Ingredient> getIngredientById(String id);

    @Query("SELECT en FROM ingredient WHERE id = :id")
    String getNameById(String id);
}
