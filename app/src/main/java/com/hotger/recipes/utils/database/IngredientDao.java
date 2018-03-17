package com.hotger.recipes.utils.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hotger.recipes.utils.model.Ingredient;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface IngredientDao {
    @Insert(onConflict = REPLACE)
    void insertAll(List<Ingredient> categories);

    @Insert(onConflict = REPLACE)
    void insert(List<Ingredient> categories);

    @Delete
    void delete(Ingredient ingredient);

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
}
