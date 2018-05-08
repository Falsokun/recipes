package com.hotger.recipes.view;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.CategoryAdapter;
import com.hotger.recipes.databinding.FragmentCategoriesBinding;
import com.hotger.recipes.database.FirebaseUtils;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.YummlyAPI;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.view.redactor.BackStackFragment;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

//TODO: красивую анимацию перехода между категориями
public class CategoryFragment extends BackStackFragment {

    FragmentCategoriesBinding mBinding;
    CategoryAdapter categoryAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Category> categoryList = getCategoryByName(YummlyAPI.Description.CUISINE);
        categoryAdapter = new CategoryAdapter((MainActivity) getActivity(), categoryList, YummlyAPI.Description.CUISINE);

//        CategoryDao dao = AppDatabase.getDatabase(getContext()).getCategoryDao();
//        if (getActivity() != null)
//            ViewModelProviders.of(getActivity())
//                    .get(CategoryViewModel.class)
//                    .getAllPrevs(dao, YummlyAPI.Description.CUISINE)
//                    .observe(getActivity(), recipePrevs -> categoryAdapter.setData(recipePrevs));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false);
        mBinding.cuisineRv.setAdapter(categoryAdapter);
        mBinding.cuisineRv.setItemAnimator(new SlideInLeftAnimator());
        mBinding.cuisineRv.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        setSpinnerAdapter();
        return mBinding.getRoot();
    }

    private void setSpinnerAdapter() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add(getString(R.string.cuisine));
        categories.add(getString(R.string.holiday));
        categories.add(getString(R.string.course));
        categories.add(getString(R.string.diet));
        mBinding.spinnerCategories.setItems(categories);
        mBinding.spinnerCategories.setOnItemSelectedListener((view, position, id, item) -> {
            switch (position) {
                case 0:
                    categoryAdapter.setData(getCategoryByName(YummlyAPI.Description.CUISINE));
                    categoryAdapter.setType(YummlyAPI.Description.CUISINE);
                    break;
                case 1:
                    categoryAdapter.setData(getCategoryByName(YummlyAPI.Description.HOLIDAY));
                    categoryAdapter.setType(YummlyAPI.Description.HOLIDAY);
                    break;
                case 2:
                    categoryAdapter.setData(getCategoryByName(YummlyAPI.Description.COURSE));
                    categoryAdapter.setType(YummlyAPI.Description.COURSE);
                    break;
                case 3:
                    categoryAdapter.setData(getCategoryByName(YummlyAPI.Description.DIET));
                    categoryAdapter.setType(YummlyAPI.Description.DIET);
                    break;
                default:
                    break;
            }
        });
    }

    public List<Category> getCategoryByName(String name) {
        AppDatabase db = AppDatabase.getDatabase(getActivity());
        List<Category> categories = new ArrayList<>();
        FirebaseUtils.addUserCategoryIfNeed(name, categories);
        if (getActivity() != null && db != null) {
            categories.addAll(db.getCategoryDao()
                    .getAllCategoriesWithDescription(name));
        }

        return categories;
    }
}
