package com.hotger.recipes.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hotger.recipes.model.Category;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface CategoryDao {

    @Insert(onConflict = REPLACE)
    void insertAll(List<Category> categories);

    @Insert(onConflict = REPLACE)
    void insert(List<Category> categories);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM category")
    List<Category> getAllCategories();

    @Query("SELECT * FROM category WHERE type LIKE :description")
    List<Category> getAllCategoriesWithDescription(String description);

    @Query("SELECT enTitle FROM category WHERE type LIKE :description")
    List<String> getEnCategoriesWithDescription(String description);

    @Query("SELECT ruTitle FROM category WHERE type LIKE :description")
    List<String> geRuCategoriesWithDescription(String description);

    @Query("SELECT * FROM category WHERE enTitle = :title OR ruTitle = :title")
    List<Category> getCategoryByName(String title);

    @Query("SELECT * FROM category WHERE searchValue LIKE :id")
    List<Category> getCategoryById(String id);
}
