package com.hotger.recipes.viewmodel;


import android.databinding.Bindable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;

import com.hotger.recipes.BR;
import com.hotger.recipes.R;
import com.hotger.recipes.adapter.ProductsAdapter;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.view.ControllableActivity;

import java.util.ArrayList;

public class RecipeViewModel extends ViewModel {

    private Recipe mCurrentRecipe;
    private ControllableActivity activity;
    private ProductsAdapter productsAdapter;

    public RecipeViewModel(ControllableActivity activity) {
        this.activity = activity;
        mCurrentRecipe = null;
    }

    @Bindable
    public Recipe getCurrentRecipe() {
        return mCurrentRecipe;
    }

    public void setCurrentRecipe(Recipe mCurrentRecipe) {
        this.mCurrentRecipe = mCurrentRecipe;
        productsAdapter = new ProductsAdapter(activity, mCurrentRecipe.getProducts(), false, true, false);
        notifyPropertyChanged(BR.currentRecipe);
    }

    public void setInstructions(String instructions) {
        mCurrentRecipe.setPreparations(instructions);
        notifyPropertyChanged(BR.currentRecipe);
    }

    @Override
    public void OnResume() {
        activity.setUpNavigation(true);
    }

    @Override
    public void OnPause() {

    }

    public ProductsAdapter getProductsAdapter() {
        return productsAdapter;
    }

    public void addCategories(ViewGroup categoryContainer) {
        ArrayList<String> categories = getCurrentRecipe().getCategoriesTitles();
        for (int i = 0; i < categories.size(); i++) {
            TableRow row = new TableRow(activity);
            row.setId(i);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            CheckBox checkBox = new CheckBox(activity);
            checkBox.setId(i);
            checkBox.setText(categories.get(i));
            checkBox.setBackground(activity.getDrawable(R.drawable.chekox_shape));
            checkBox.setPadding(30, 0, 30, 0);
            checkBox.setButtonDrawable(null);
            row.addView(checkBox);
            categoryContainer.addView(row);
        }
    }

    public String getStringTime(int time) {
        int hours = time / 60;
        int min = time % 60;
        if (hours == 0) {
            return min + " min";
        } else {
            return hours + " : " + min;
        }
    }

    public View.OnClickListener getOnClickListener(TextView textView, boolean isPlus) {
        return v -> {
            int sign = isPlus ? 1 : -1;
            if (isPlus) {
                int portions = mCurrentRecipe.getNumberOfServings();
                int currentPortions = Integer.valueOf(textView.getText().toString()) + sign;
                if (currentPortions == 0)
                    return;

                textView.setText(String.valueOf(currentPortions));
                productsAdapter.setKoeff(currentPortions / portions);
            }
        };
    }

    public void deleteRecipeFromDatabase(ControllableActivity activity, String recipeId) {
        AppDatabase db = AppDatabase.getDatabase(activity);
        db.getRecipeDao().deleteById(recipeId);
        db.getRelationCategoryRecipeDao().deleteAllWithId(recipeId);
        db.getRecipePrevDao().deleteAllById(recipeId);
    }
}
