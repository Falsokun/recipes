package com.hotger.recipes.utils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.hotger.recipes.database.relations.RelationCategoryRecipe;
import com.hotger.recipes.database.relations.RelationObj;
import com.hotger.recipes.database.relations.RelationRecipeType;
import com.hotger.recipes.database.dao.CategoryDao;
import com.hotger.recipes.database.dao.RelationsDao;
import com.hotger.recipes.database.dao.IngredientDao;
import com.hotger.recipes.database.dao.ProductDao;
import com.hotger.recipes.database.dao.RecipeDao;
import com.hotger.recipes.database.dao.RecipePrevDao;
import com.hotger.recipes.database.dao.RelationCategoryRecipeDao;
import com.hotger.recipes.database.dao.RelationRecipeTypeDao;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.model.Ingredient;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.RecipeNF;
import com.hotger.recipes.model.RecipePrev;

/**
 * Room database class
 */
@Database(entities = { Category.class, RecipePrev.class, Ingredient.class,
        Product.class, RecipeNF.class, RelationCategoryRecipe.class, RelationObj.class,
        RelationRecipeType.class}, version = 1, exportSchema = false)
public abstract class  AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract CategoryDao getCategoryDao();

    public abstract RecipePrevDao getRecipePrevDao();

    public abstract IngredientDao getIngredientDao();

    public abstract ProductDao getProductDao();

    public abstract RecipeDao getRecipeDao();

    public abstract RelationCategoryRecipeDao getRelationCategoryRecipeDao();

    public abstract RelationRecipeTypeDao getRelationRecipeTypeDao();

    public abstract RelationsDao getRelationsDao();

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context,
                            AppDatabase.class, "populus-database").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}
