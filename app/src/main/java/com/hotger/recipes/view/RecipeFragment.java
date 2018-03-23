package com.hotger.recipes.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentRecipeShowBinding;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.RedactorActivity;
import com.hotger.recipes.viewmodel.RecipeViewModel;

public class RecipeFragment extends Fragment {

    private FragmentRecipeShowBinding mBinding;
    private RecipeViewModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int id = getArguments().getInt(Utils.RECIPE_ID, -1);
            if (id != -1) {
                model = new RecipeViewModel(id, (ControllableActivity) getActivity());
            } else {
                Recipe recipe = (Recipe) getArguments().getSerializable(Utils.RECIPE_OBJ);
                model = new RecipeViewModel(recipe, (ControllableActivity) getActivity());
            }
        }

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_show, container, false);
        mBinding.setModel(model);
        mBinding.products.setAdapter(model.getProductsAdapter());
        mBinding.products.setLayoutManager(new LinearLayoutManager(getContext()));
        initWaveView();
        model.addCategories(mBinding.categoryContainer);
        ((ControllableActivity) getActivity()).updateCollapsing(((ControllableActivity)getActivity()).getAppBar(), true);
        ((ControllableActivity) getActivity()).setToolbarImage(model.getCurrentRecipe().getImageURL());
//        ((ControllableActivity) getActivity()).updateTitle(model.getCurrentRecipe().getName());
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        if (getArguments() != null
                && getArguments().getSerializable(Utils.RECIPE_OBJ) != null) {
            inflater.inflate(R.menu.menu_fragment, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_edit) {
            Intent intent = new Intent(getActivity(), RedactorActivity.class);
            intent.putExtra(Utils.RECIPE_ID, model.getCurrentRecipe().getId());
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_delete) {
            model.deleteRecipeFromDatabase((ControllableActivity) getActivity(),
                    model.getCurrentRecipe().getId());
            getActivity().onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initWaveView() {
        Recipe recipe = model.getCurrentRecipe();
        if (recipe.getTotalTimeInMinutes() == 0) {
            mBinding.timing.setVisibility(View.GONE);
            mBinding.divider1.setVisibility(View.GONE);
            return;
        }

        mBinding.prepTime.setAnimDuration(3000);
        mBinding.cookingTime.setAnimDuration(3000);
        mBinding.totalTime.setAnimDuration(3000);
        mBinding.prepTime.setProgressValue(recipe.getPrepTimeMinutes());
        mBinding.cookingTime.setProgressValue(recipe.getCookingTimeInMinutes());
        mBinding.totalTime.setProgressValue(recipe.getTotalTimeInMinutes());
        mBinding.prepTime.setCenterTitle(model.getStringTime(recipe.getPrepTimeMinutes()));
        mBinding.cookingTime.setCenterTitle(model.getStringTime(recipe.getCookingTimeInMinutes()));
        mBinding.totalTime.setCenterTitle(model.getStringTime(recipe.getTotalTimeInMinutes()));
    }

    @Override
    public void onResume() {
        super.onResume();
        model.OnResume();
    }
}