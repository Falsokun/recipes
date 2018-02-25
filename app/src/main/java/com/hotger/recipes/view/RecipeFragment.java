package com.hotger.recipes.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentRecipeShowBinding;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.RedactorActivity;
import com.hotger.recipes.viewmodel.RecipeViewModel;

public class RecipeFragment extends Fragment {

    private FragmentRecipeShowBinding mBinding;
    private RecipeViewModel model;
    private MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int id = getArguments().getInt(Utils.RECIPE_ID, -1);
        activity = (MainActivity) getActivity();
        model = new RecipeViewModel(id, activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_show, container, false);
        mBinding.setModel(model);
        mBinding.products.setAdapter(model.getProductsAdapter());
        mBinding.products.setLayoutManager(new LinearLayoutManager(getContext()));
        model.addCategories(mBinding.categoryContainer);
        ((MainActivity) getActivity()).updateCollapsing(true);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        model.OnResume();
    }

    public void editRecipe() {
        //TODO АРГУМЕНТЫ ДЛЯ АЙДИ
//        Bundle bundle = new Bundle();
//        bundle.putInt(Utils.RECIPE_ID, (int) model.getCurrentRecipe().getId());
        Intent intent = new Intent(getContext(), RedactorActivity.class);
        startActivity(intent);
    }
}