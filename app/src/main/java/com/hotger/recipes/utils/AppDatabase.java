package com.hotger.recipes.utils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.hotger.recipes.utils.database.CategoryDao;
import com.hotger.recipes.utils.database.IngredientDao;
import com.hotger.recipes.utils.database.ProductDao;
import com.hotger.recipes.utils.database.RecipePrevDao;
import com.hotger.recipes.utils.model.Category;
import com.hotger.recipes.utils.model.Ingredient;
import com.hotger.recipes.utils.model.Product;
import com.hotger.recipes.utils.model.RecipePrev;

@Database(entities = { Category.class, RecipePrev.class, Ingredient.class, Product.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CategoryDao getCategoryDao();

    public abstract RecipePrevDao getRecipePrevDao();

    public abstract IngredientDao getIngredientDao();

    public abstract ProductDao getProductDao();
}
