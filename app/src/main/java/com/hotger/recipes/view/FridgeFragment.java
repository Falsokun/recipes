package com.hotger.recipes.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentFridgeBinding;
import com.hotger.recipes.viewmodel.InputProductsViewModel;
import com.hotger.recipes.view.redactor.BackStackFragment;

public class FridgeFragment extends BackStackFragment {

    FragmentFridgeBinding mBinding;
    InputProductsViewModel inputModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fridge, container, false);
        mBinding.setModel(inputModel);
        mBinding.fragmentRedactor.listView.setAdapter(inputModel.getDataHintAdapter());
        mBinding.fragmentRedactor.productsLineRv.setAdapter(inputModel.getProductsAdapter());
        mBinding.fragmentRedactor.productsLineRv.setLayoutManager(new LinearLayoutManager(getContext()));
        initListeners();
        return mBinding.getRoot();
    }

    private void initListeners() {
        mBinding.searchBtn.setOnClickListener(view -> ((MainActivity) getActivity()).searchForRecipeWithIngridients(inputModel.getProductsAdapter().getData()));
        mBinding.fragmentRedactor.addProductName.addTextChangedListener(inputModel.getProductTextChangeListener());
        mBinding.fragmentRedactor.listView.setOnItemClickListener(inputModel.getOnHintItemClickListener(mBinding.fragmentRedactor.addProductName));
        mBinding.fragmentRedactor.addProductName.setOnEditorActionListener(inputModel.getOnEditorActionListener(mBinding.fragmentRedactor.listView.getCount()));
    }

    public void setInputModel(InputProductsViewModel model) {
        this.inputModel = model;
    }
}
