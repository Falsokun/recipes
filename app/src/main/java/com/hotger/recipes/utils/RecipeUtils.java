package com.hotger.recipes.utils;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hotger.recipes.App;
import com.hotger.recipes.database.RelationCategoryRecipe;
import com.hotger.recipes.database.RelationRecipeType;
import com.hotger.recipes.database.FirebaseUtils;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.model.GsonModel.Image;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.view.ControllableActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecipeUtils {
    public static void saveToDatabase(Recipe currentRecipe, ControllableActivity activity,
                                      boolean shouldSaveToFirebase, ArrayList<String> categoryTitles,
                                      String type, boolean isBookmark) {
        if (currentRecipe.getImageURL() == null) {
            currentRecipe.setImageURL(FirebaseUtils.NO_IMAGE_URL);
        }

        AppDatabase db = AppDatabase.getDatabase(activity);
        if (shouldSaveToFirebase) {
            saveImageToCloudFirebase(currentRecipe, currentRecipe.getRecipe().getImageUrl());
        }

        if (currentRecipe.getId() == null) {
            currentRecipe.setId(currentRecipe.getName() + UUID.randomUUID().toString());
        }

        db.getRecipeDao().insert(currentRecipe.getRecipe());
        for(Product product : currentRecipe.getProducts()) {
            product.setRecipeId(currentRecipe.getId());
        }

        if (Utils.isRussian() && !isBookmark) {
            currentRecipe.setLang("ru");
        }

        db.getRelationRecipeTypeDao().insert(new RelationRecipeType(currentRecipe.getId(), type));
        if (categoryTitles == null && currentRecipe.getCategories() != null) {
            categoryTitles = new ArrayList<>();
            for (Category category : currentRecipe.getCategories()) {
                categoryTitles.add(category.getTitle());
            }
        }

        List<RelationCategoryRecipe> relations = createRelationTable(db, currentRecipe, categoryTitles);
        db.getProductDao().insert(currentRecipe.getProducts());

        if (!isBookmark) {
            RecipePrev prev = new RecipePrev(currentRecipe.getId(),
                    new Image(currentRecipe.getRecipe().getImageUrl()),
                    currentRecipe.getName(),
                    currentRecipe.getRecipe().getTotalTimeInSeconds(),
                    false);
            db.getRecipePrevDao().insert(prev);

            if (shouldSaveToFirebase) {
                FirebaseUtils.saveRecipeToFirebase(currentRecipe, relations, prev);
            }
        }
    }

    private static List<RelationCategoryRecipe> createRelationTable(AppDatabase db, Recipe currentRecipe, List<String> categoryTitles) {
        List<RelationCategoryRecipe> categoryRelations = new ArrayList<>();
        db.getRelationCategoryRecipeDao().deleteAllWithId(currentRecipe.getId());
        for (String category : categoryTitles) {
            String catId = db.getCategoryDao().getCategoryByName(category).get(0).getSearchValue();
            RelationCategoryRecipe relation = new RelationCategoryRecipe(currentRecipe.getId(), catId);
            db.getRelationCategoryRecipeDao().insert(relation);
            categoryRelations.add(relation);
        }

        return categoryRelations;
    }

    //А если изображение с таким именем существует?
    private static void saveImageToCloudFirebase(Recipe currentRecipe, String path) {
        String name = Utils.FIREBASE_IMG_STORAGE + path.split("/")[path.split("/").length - 1];
        StorageReference storageRef = App.getStorage().getReference();
        StorageReference mountainsRef = storageRef.child(name);
        try {
            InputStream stream = new FileInputStream(new File(path));
            UploadTask uploadTask = mountainsRef.putStream(stream);
            uploadTask.addOnFailureListener(exception -> {
            }).addOnSuccessListener(taskSnapshot -> {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                currentRecipe.setImageURL(downloadUrl.toString());
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void deleteBookmarkFromDatabase(Recipe recipe, ControllableActivity activity, boolean delPrev) {
        AppDatabase db = AppDatabase.getDatabase(activity);
        db.getRecipeDao().deleteById(recipe.getId());
        db.getProductDao().removeWhereId(recipe.getId());
        db.getRelationRecipeTypeDao().deleteWhereId(recipe.getId()); // нужно еще тип, пушо говно
    }
}
