package com.hotger.recipes.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.ActivityShoppingListBinding;
import com.hotger.recipes.viewmodel.InputProductsViewModel;

import java.util.ArrayList;


public class ShoppingListActivity extends ControllableActivity {

    private ActivityShoppingListBinding mBinding;
    InputProductsViewModel inputModel;
    public static String SHOPPING_LIST_ID = "shopping_list_id";
    public static String SHOPPING_LIST_CHECKED = "shopping_list_checked";
    public static String SHOPPING_LIST_UNCHECKED = "shopping_list_un_checked";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_shopping_list);
        inputModel = new InputProductsViewModel(this, new ArrayList<>(), true, false, true);
        mBinding.setModel(inputModel);
        setSupportActionBar(mBinding.toolBar);
        getSupportActionBar().setTitle(getString(R.string.shopping_list));
        mBinding.fragmentRedactor.setIsEmpty(inputModel.getDataHintAdapter().isEmptyData());
        mBinding.fragmentRedactor.listView.setAdapter(inputModel.getDataHintAdapter());
        mBinding.fragmentRedactor.productsLineRv.setAdapter(inputModel.getProductsAdapter());
        mBinding.fragmentRedactor.productsLineRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.fragmentRedactor.productsLineRv.setHasFixedSize(true);
        inputModel.getItemTouchListener().attachToRecyclerView(mBinding.fragmentRedactor.productsLineRv);
        initListeners();
        setUpNavigation(true);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shopping, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_clear:
                inputModel.getProductsAdapter().removeCheckedItems();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initListeners() {
        mBinding.fragmentRedactor.addProductName.addTextChangedListener(inputModel
                .getProductTextChangeListener());
        mBinding.fragmentRedactor.listView.setOnItemClickListener(inputModel
                .getOnHintItemClickListener(mBinding.fragmentRedactor.addProductName));
        mBinding.fragmentRedactor.addProductName.setOnEditorActionListener(inputModel
                .getOnEditorActionListener());
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
