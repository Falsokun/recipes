package com.hotger.recipes.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageView;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.ActivityShoppingListBinding;
import com.hotger.recipes.viewmodel.InputProductsViewModel;

import java.util.ArrayList;


public class ShoppingListActivity extends ControllableActivity {

    ActivityShoppingListBinding mBinding;
    InputProductsViewModel inputModel;
    public static String SHOPPING_LIST_ID = "shopping_list_id";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_shopping_list);
        inputModel = new InputProductsViewModel(this, new ArrayList<>(), true, false, true);
        mBinding.setModel(inputModel);
        mBinding.fragmentRedactor.listView.setAdapter(inputModel.getDataHintAdapter());
        mBinding.fragmentRedactor.productsLineRv.setAdapter(inputModel.getProductsAdapter());
        mBinding.fragmentRedactor.productsLineRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.fragmentRedactor.productsLineRv.setHasFixedSize(true);
        inputModel.getItemTouchListener().attachToRecyclerView(mBinding.fragmentRedactor.productsLineRv);
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inputModel.restoreListData(this, SHOPPING_LIST_ID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        inputModel.saveListData(this, SHOPPING_LIST_ID);
    }

    private void initListeners() {
        mBinding.fragmentRedactor.addProductName.addTextChangedListener(inputModel
                .getProductTextChangeListener());
        mBinding.fragmentRedactor.listView.setOnItemClickListener(inputModel
                .getOnHintItemClickListener(mBinding.fragmentRedactor.addProductName));
        mBinding.fragmentRedactor.addProductName.setOnEditorActionListener(inputModel
                .getOnEditorActionListener(mBinding.fragmentRedactor.listView.getCount()));
    }

    @Override
    public Fragment getCurrentFragment() {
        return null;
    }

    @Override
    public ImageView getToolbarImageView() {
        return null;
    }

    @Override
    public AppBarLayout getAppBar() {
        return null;
    }
}
