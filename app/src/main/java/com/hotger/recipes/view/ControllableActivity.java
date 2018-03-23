package com.hotger.recipes.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hotger.recipes.App;
import com.hotger.recipes.R;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.DisableAppBarLayoutBehavior;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.model.RecipeNF;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ControllableActivity extends AppCompatActivity {

    public abstract Fragment getCurrentFragment();

    public abstract AppDatabase getDatabase();

    public abstract ImageView getToolbarImageView();

    public void setCurrentFragment(Fragment fragment, boolean addToBackStack, String name) {
        Fragment curFragment = getCurrentFragment();
        FragmentManager fm = curFragment.getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_container, fragment, name);
        if (addToBackStack) {
            ft.addToBackStack(name);
        }

        ft.commit();
        setUpNavigation(addToBackStack);
    }

    public void openRecipe(String recipeId) {
        App.getApi()
                .getRecipeByID(recipeId)
                .enqueue(new Callback<RecipeNF>() {
                             @Override
                             public void onResponse(@NonNull Call<RecipeNF> call, @NonNull Response<RecipeNF> response) {
                                 if (response.body() == null) {
                                     Toast.makeText(ControllableActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                     return;
                                 }

                                 Recipe recipe = new Recipe((RecipeNF) response.body(), ControllableActivity.this);

                                 RecipeFragment fragment = new RecipeFragment();
                                 Bundle bundle = new Bundle();
                                 recipe.prepareDataForShowing(ControllableActivity.this);
                                 bundle.putSerializable(Utils.RECIPE_OBJ, recipe);
                                 fragment.setArguments(bundle);

                                 setCurrentFragment(fragment, true, fragment.getTag());
                             }

                             @Override
                             public void onFailure(@NonNull Call<RecipeNF> call, @NonNull Throwable t) {

                             }
                         }
                );

    }

    public void updateToolbar(Fragment fragment) {
        FragmentManager fm = fragment.getChildFragmentManager();
        if (fm.getBackStackEntryCount() != 0) {
            setUpNavigation(true);
        } else {
            setUpNavigation(false);
        }
    }

    public void setUpNavigation(boolean value) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(value);
            getSupportActionBar().setDisplayHomeAsUpEnabled(value);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public void setToolbarImage(String imgPath) {
        Glide.with(this).load(imgPath).into(getToolbarImageView());
    }

    public void updateCollapsing(AppBarLayout appbar, boolean enabled) {
        appbar.setExpanded(false, false);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        if (layoutParams.getBehavior() != null) {
            ((DisableAppBarLayoutBehavior) layoutParams.getBehavior()).setEnabled(enabled);
        }
    }

    public abstract AppBarLayout getAppBar();

    public void openRecipeFromDB(String id) {
        Recipe recipe = getRecipeFromDBByID(id);
        RecipeFragment fragment = new RecipeFragment();
        Bundle bundle = new Bundle();
//        recipe.prepareDataForShowing(ControllableActivity.this);
        bundle.putSerializable(Utils.RECIPE_OBJ, recipe);
        fragment.setArguments(bundle);

        setCurrentFragment(fragment, true, fragment.getTag());
    }

    public Recipe getRecipeFromDBByID(String id) {
        Recipe recipe = getDatabase().getRecipeDao().getRecipesById(id).get(0);
        List<String> ids = getDatabase().getRelationDao().getCategoryIdsForRecipe(id);
        ArrayList<Category> categories = new ArrayList<>();
        for (String catId : ids) {
            categories.add(getDatabase().getCategoryDao().getCategoryById(catId).get(0));
        }

        recipe.setCategories(categories);
        return recipe;
    }
}
