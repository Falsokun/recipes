package com.hotger.recipes.viewmodel;

import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hotger.recipes.App;
import com.hotger.recipes.R;
import com.hotger.recipes.database.RelationCategoryRecipe;
import com.hotger.recipes.database.RelationRecipeType;
import com.hotger.recipes.firebase.FirebaseUtils;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.model.GsonModel.Image;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.ControllableActivity;
import com.shawnlin.numberpicker.NumberPicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class RedactorViewModel extends ViewModel {

    private ControllableActivity activity;

    private Recipe currentRecipe;

    private boolean isEdited;

    private InputProductsViewModel inputProductsViewModel;

    private ArrayList<String> categoryTitles = new ArrayList<>();

    public RedactorViewModel(ControllableActivity activity) {
        this.activity = activity;
        currentRecipe = new Recipe();
        isEdited = false;
        inputProductsViewModel = new InputProductsViewModel(activity, currentRecipe.getProducts(), true, true);
    }

    public RedactorViewModel(ControllableActivity activity, String id) {
        this.activity = activity;
        currentRecipe = activity.getRecipeFromDBByID(id);
        isEdited = false;
        inputProductsViewModel = new InputProductsViewModel(activity, currentRecipe.getProducts(), true, true);
        for(Category category : currentRecipe.getCategories()) {
            categoryTitles.add(category.getTitle());
        }
    }

    //region listeners
    public CompoundButton.OnCheckedChangeListener getCheckedListener() {
        return (compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                categoryTitles.add((String) compoundButton.getTag());
                compoundButton.setBackground(activity.getResources()
                        .getDrawable(R.drawable.chekox_shape_colored));
            } else {
                categoryTitles.remove(compoundButton.getTag());
                compoundButton.setBackground(activity.getResources()
                        .getDrawable(R.drawable.chekox_shape));
            }
        };
    }

    public NumberPicker.OnValueChangeListener getPickerChangedListener() {
        return (picker, oldVal, newVal) ->
                currentRecipe.setNumberOfServings(newVal);
    }
    //endregion

    public void onSave(ViewPager viewPager) {
        if (!isDataCorrect(viewPager)) {
            return;
        }

        saveToDatabase(activity);
    }

    public boolean isDataCorrect(ViewPager viewPager) {
        int errorType = -1;
        if (currentRecipe.getName() == null) {
            Toast.makeText(activity,
                    activity.getResources().getString(R.string.fill_data) + " name ", Toast.LENGTH_SHORT).show();
            return false;
        } else if (currentRecipe.getProducts().size() == 0) {
            errorType = 0;
        } else if (currentRecipe.getPreparations() == null) {
            errorType = 1;
        } else if (currentRecipe.getTotalTimeInMinutes() == 0) {
            errorType = 4;
        }

        if (errorType > -1) {
            Toast.makeText(activity,
                    activity.getResources().getString(R.string.fill_data) + errorType, Toast.LENGTH_SHORT).show();
            viewPager.setCurrentItem(errorType);
            return false;
        }

        return true;
    }

    @Override
    public void OnResume() {

    }

    @Override
    public void OnPause() {

    }

    public void saveToDatabase(ControllableActivity activity) {
        if (currentRecipe.getImageURL() == null) {
            currentRecipe.setImageURL(FirebaseUtils.NO_IMAGE_URL);
        }

        AppDatabase db = AppDatabase.getDatabase(activity);
        saveImageToCloudFirebase(currentRecipe.getRecipe().getImageUrl());
        if (currentRecipe.getId() == null) {
            currentRecipe.setId(currentRecipe.getName() + UUID.randomUUID().toString());
        }

        db.getRecipeDao().insert(currentRecipe.getRecipe());
        for(Product product : currentRecipe.getProducts()) {
            product.setRecipeId(currentRecipe.getId());
        }

        if (Utils.isRussian()) {
            currentRecipe.setLang("ru");
        }

        db.getRelationRecipeTypeDao().insert(new RelationRecipeType(currentRecipe.getId(), Utils.TYPE.TYPE_MY_RECIPES));
        createRelationTable(db);
        db.getProductDao().insert(currentRecipe.getProducts());
        RecipePrev prev = new RecipePrev(currentRecipe.getId(),
                new Image(currentRecipe.getRecipe().getImageUrl()),
                currentRecipe.getName(),
                String.valueOf(currentRecipe.getRecipe().getTotalTimeInSeconds()));
        db.getRecipePrevDao().insert(prev);
    }

    private void createRelationTable(AppDatabase db) {
        db.getRelationCategoryRecipeDao().deleteAllWithId(currentRecipe.getId());
        for (String category : categoryTitles) {
            String catId = db.getCategoryDao().getCategoryByName(category).get(0).getSearchValue();
            db.getRelationCategoryRecipeDao().insert(new RelationCategoryRecipe(currentRecipe.getId(), catId));
        }
    }

    private void saveImageToCloudFirebase(String path) {
        String name = Utils.FIREBASE_IMG_STORAGE + path.split("/")[path.split("/").length - 1];
        StorageReference storageRef = App.getStorage().getReference();
        StorageReference mountainsRef = storageRef.child(name);
        try {
            InputStream stream = new FileInputStream(new File(path));
            UploadTask uploadTask = mountainsRef.putStream(stream);
            uploadTask.addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
            }).addOnSuccessListener(taskSnapshot -> {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                currentRecipe.setImageURL(downloadUrl.toString());
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //region getters setters
    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public InputProductsViewModel getInputProductsViewModel() {
        return inputProductsViewModel;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        this.isEdited = edited;
    }
    //endregion
}
