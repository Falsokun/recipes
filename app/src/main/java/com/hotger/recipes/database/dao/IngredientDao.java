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

    @Query("SELECT * FROM ingredient WHERE en = :name OR ru = :name")
    List<Ingredient> getIngredientByName(String name);

    @Query("SELECT * FROM ingredient WHERE en = :id")
    List<Ingredient> getIngredientById(String id);

    @Query("Select * FROM ingredient WHERE :s LIKE '%' || en || '%'")
    List<Ingredient> getIngredientLike(String s);

    @Query("Select * FROM ingredient LIMIT :start, :range")
    List<Ingredient> getIngredientRange(int start, int range);
}
