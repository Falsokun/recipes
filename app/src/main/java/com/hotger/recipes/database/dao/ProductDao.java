package com.hotger.recipes.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.hotger.recipes.model.Product;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Dao for products
 * Fields: recipeId (FK), ingredientId (PK), amount, measure
 */
@Dao
public interface ProductDao extends BaseDao<Product> {
    @Insert(onConflict = REPLACE)
    void insertAll(List<Product> products);

    @Insert(onConflict = REPLACE)
    void insert(List<Product> products);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM product WHERE recipeId LIKE :recipeId")
    List<Product> getProducts(String recipeId);

    @Query("DELETE FROM product WHERE recipeId = :id")
    void removeWhereId(String id);

    @Query("DELETE FROM product WHERE recipeId = :recipeId AND ingredientId = :ingredientId")
    void deleteWhereId(String recipeId, String ingredientId);
}
