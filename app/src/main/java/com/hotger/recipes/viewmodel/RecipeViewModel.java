package com.hotger.recipes.viewmodel;


import android.databinding.Bindable;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableRow;

import com.hotger.recipes.BR;
import com.hotger.recipes.R;
import com.hotger.recipes.adapter.ProductsAdapter;
import com.hotger.recipes.utils.model.Recipe;
import com.hotger.recipes.view.ControllableActivity;

import java.util.ArrayList;

public class RecipeViewModel extends MViewModel {

    private Recipe mCurrentRecipe;
    private ControllableActivity activity;
    private ProductsAdapter productsAdapter;

    public RecipeViewModel(int recipeId, ControllableActivity activity) {
        this.activity = activity;
        productsAdapter = new ProductsAdapter(activity, mCurrentRecipe.getProducts(), false, true);
    }

    public RecipeViewModel(Recipe recipe, ControllableActivity activity) {
        this.activity = activity;
        mCurrentRecipe = recipe;
        productsAdapter = new ProductsAdapter(activity, mCurrentRecipe.getProducts(), false, true);
    }

    @Bindable
    public Recipe getCurrentRecipe() {
        return mCurrentRecipe;
    }

    public void setCurrentRecipe(Recipe mCurrentRecipe) {
        this.mCurrentRecipe = mCurrentRecipe;
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
        ArrayList<String> categories = getCurrentRecipe().getCategories();
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
}
