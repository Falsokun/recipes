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
import com.hotger.recipes.utils.RealmString;
import com.hotger.recipes.utils.Recipe;
import com.hotger.recipes.view.MainActivity;

import io.realm.RealmList;

public class RecipeViewModel extends ViewModel {

    private Recipe mCurrentRecipe;
    private MainActivity activity;
    private ProductsAdapter productsAdapter;

    public RecipeViewModel(int recipeId, MainActivity activity) {
        this.activity = activity;
        mCurrentRecipe = activity
                .getRealmInstance()
                .where(Recipe.class)
                .equalTo("id", recipeId)
                .findAll()
                .get(0);
        productsAdapter = new ProductsAdapter(activity, mCurrentRecipe.getProducts(), false);
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
        activity.setToolbarImage();
        activity.setUpNavigation(true);
    }

    @Override
    public void OnPause() {

    }

    public ProductsAdapter getProductsAdapter() {
        return productsAdapter;
    }

    public void addCategories(ViewGroup categoryContainer) {
        RealmList<RealmString> categories = getCurrentRecipe().getCategories();
        for (int i = 0; i < categories.size(); i++) {
            TableRow row = new TableRow(activity);
            row.setId(i);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            CheckBox checkBox = new CheckBox(activity);
            checkBox.setId(i);
            checkBox.setText(categories.get(i).toString());
            checkBox.setBackground(activity.getDrawable(R.drawable.chekox_shape));
            checkBox.setPadding(30, 0, 30, 0);
            checkBox.setButtonDrawable(null);
            row.addView(checkBox);
            categoryContainer.addView(row);
        }
    }
}
