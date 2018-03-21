package com.hotger.recipes.viewmodel;

import android.databinding.BaseObservable;

import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.YummlyAPI;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.view.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends BaseObservable {

    private SearchActivity activity;

    private AppDatabase db;

    /**
     * Search parameters
     */
    private ArrayList<String> cuisines = new ArrayList<>();
    private ArrayList<String> course = new ArrayList<>();
    private ArrayList<String> diets = new ArrayList<>();
    private int time;

    public SearchViewModel(SearchActivity activity, AppDatabase db) {
        this.activity = activity;
        this.db = db;
    }

    public String getSearchValue() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getCategoryString(YummlyAPI.ALLOWED_COURSE, course, YummlyAPI.Description.COURSE));
        stringBuilder.append(getCategoryString(YummlyAPI.ALLOWED_CUISINE_PARAM, cuisines, YummlyAPI.Description.CUISINE));
        stringBuilder.append(getCategoryString(YummlyAPI.ALLOWED_DIET_PARAM, diets, YummlyAPI.Description.DIET));
        stringBuilder.append("&maxResult=" + YummlyAPI.MAX_RESULT);
        stringBuilder.replace(0, 1, "");
        return stringBuilder.toString();
    }

    private String getCategoryString(String category, ArrayList<String> selected, String type) {
        List<Category> course = db.getCategoryDao().getAllCategoriesWithDescription(type);
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : selected) {
            stringBuilder.append(category);
            stringBuilder.append(getCategoryName(str, course));
        }

        return stringBuilder.toString();
    }

    private String getCategoryName(String categoryName, List<Category> categories) {
        for (Category category : categories) {
            if (category.getTitle().equals(categoryName))
                return category.getSearchValue();
        }

        return "";
    }

    public ArrayList<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(ArrayList<String> cuisines) {
        this.cuisines = cuisines;
    }

    public ArrayList<String> getCourse() {
        return course;
    }

    public void setCourse(ArrayList<String> course) {
        this.course = course;
    }

    public ArrayList<String> getDiets() {
        return diets;
    }

    public void setDiets(ArrayList<String> diets) {
        this.diets = diets;
    }
}
