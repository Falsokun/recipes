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
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.DisableAppBarLayoutBehavior;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.utils.model.Recipe;

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
                .enqueue(new Callback<Recipe>() {
                             @Override
                             public void onResponse(@NonNull Call<Recipe> call, @NonNull Response<Recipe> response) {
                                 Recipe recipe = response.body();
                                 if (recipe == null) {
                                     Toast.makeText(ControllableActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                     return;
                                 }

                                 RecipeFragment fragment = new RecipeFragment();
                                 Bundle bundle = new Bundle();
//                                 recipe.setFromAPI(true);
                                 recipe.prepareDataForShowing();
                                 bundle.putSerializable(Utils.RECIPE_OBJ, recipe);
                                 fragment.setArguments(bundle);

                                 setCurrentFragment(fragment, true, fragment.getTag());
                             }

                             @Override
                             public void onFailure(@NonNull Call<Recipe> call, @NonNull Throwable t) {

                             }
                         }
                );

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
}
