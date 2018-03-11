package com.hotger.recipes.utils.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hotger.recipes.utils.model.Category;

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

    @Query("SELECT * FROM category WHERE description LIKE :description")
    List<Category> getAllCategoriesWithDescription(String description);
}
