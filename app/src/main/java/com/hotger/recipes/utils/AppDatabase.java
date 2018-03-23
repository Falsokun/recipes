package com.hotger.recipes.utils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.hotger.recipes.database.CategoryDao;
import com.hotger.recipes.database.IngredientDao;
import com.hotger.recipes.database.ProductDao;
import com.hotger.recipes.database.RecipeDao;
import com.hotger.recipes.database.RecipePrevDao;
import com.hotger.recipes.database.RelationDao;
import com.hotger.recipes.database.RelationTable;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.model.Ingredient;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.RecipeNF;
import com.hotger.recipes.model.RecipePrev;

@Database(entities = { Category.class, RecipePrev.class, Ingredient.class, Product.class, RecipeNF.class, RelationTable.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract CategoryDao getCategoryDao();

    public abstract RecipePrevDao getRecipePrevDao();

    public abstract IngredientDao getIngredientDao();

    public abstract ProductDao getProductDao();

    public abstract RecipeDao getRecipeDao();

    public abstract RelationDao getRelationDao();

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context,
                            AppDatabase.class, "populus-database").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}
