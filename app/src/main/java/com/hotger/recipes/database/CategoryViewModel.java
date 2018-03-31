package com.hotger.recipes.database;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.hotger.recipes.database.dao.CategoryDao;
import com.hotger.recipes.model.Category;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private LiveData<List<Category>> categories;

    public LiveData<List<Category>> getAllPrevs(CategoryDao dao, String name) {
        if (categories == null) {
//            categories = dao.getAllCategories();
        }
        return categories;
    }
}
