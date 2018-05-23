package com.hotger.recipes.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hotger.recipes.App;
import com.hotger.recipes.R;
import com.hotger.recipes.model.ApiRecipe;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.UI.DisableAppBarLayoutBehavior;
import com.hotger.recipes.utils.ParseUtils;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ControllableActivity extends AppCompatActivity {

    public abstract Fragment getCurrentFragment();

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
        openRecipe(recipeId, false);
    }

    public void openRecipe(String recipeId, boolean addGestures) {
        RecipeFragment fragment = new RecipeFragment();
        if (addGestures) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Utils.IntentVars.INIT_GESTURES, true);
            fragment.setArguments(bundle);
        }

        setCurrentFragment(fragment, true, RecipeFragment.class.getName());
        loadRecipe(recipeId);
    }

    public void loadRecipe(String recipeId) {
        App.getApi()
                .getRecipeByID(recipeId)
                .enqueue(new Callback<ApiRecipe>() {
                             @Override
                             public void onResponse(@NonNull Call<ApiRecipe> call, @NonNull Response<ApiRecipe> response) {
                                 if (response.body() == null) {
                                     Toast.makeText(ControllableActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                     return;
                                 }

                                 Recipe recipe = new Recipe(response.body(), ControllableActivity.this);
                                 Intent intent = new Intent(Utils.IntentVars.RECIPE_OBJ);
                                 intent.putExtra(Utils.IntentVars.RECIPE_OBJ, recipe);
                                 LocalBroadcastManager.getInstance(ControllableActivity.this).sendBroadcast(intent);
                                 ParseUtils.parseRecipe(recipe.getRecipe().getSource().getSourceRecipeUrl(),
                                         ControllableActivity.this, true);
                             }

                             @Override
                             public void onFailure(@NonNull Call<ApiRecipe> call, @NonNull Throwable t) {

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

    public void openRecipeFromDB(String id, boolean addGestures) {
        Recipe recipe = getRecipeFromDBByID(id);
        RecipeFragment fragment = new RecipeFragment();
        Bundle bundle = new Bundle();
//        recipe.prepareDataForShowing(ControllableActivity.this);
        if (addGestures) {
            bundle.putBoolean(Utils.IntentVars.INIT_GESTURES, true);
        }

        bundle.putSerializable(Utils.IntentVars.RECIPE_OBJ, recipe);
        fragment.setArguments(bundle);

        setCurrentFragment(fragment, true, RecipeFragment.class.getName());
    }

    public void openRecipeFromDB(String id) {
        openRecipeFromDB(id, false);
    }

    public Recipe getRecipeFromDBByID(String id) {
        Recipe recipe = AppDatabase.getDatabase(this).getRecipeDao().getRecipesById(id).get(0);
        List<String> ids = AppDatabase.getDatabase(this).getRelationCategoryRecipeDao().getCategoryIdsForRecipe(id);
        ArrayList<Category> categories = new ArrayList<>();
        for (String catId : ids) {
            categories.add(AppDatabase.getDatabase(this).getCategoryDao().getCategoryById(catId).get(0));
        }

        recipe.setCategories(categories);
        return recipe;
    }

    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void loadRecipeFromDB(String id) {
        Recipe recipe = getRecipeFromDBByID(id);
        Intent intent = new Intent(Utils.IntentVars.RECIPE_OBJ);
        intent.putExtra(Utils.IntentVars.RECIPE_OBJ, recipe);
        intent.putExtra(Utils.IntentVars.SHOULD_WAIT, false);
        LocalBroadcastManager.getInstance(ControllableActivity.this).sendBroadcast(intent);
    }

    public void openSearch() {

    }
}
