package com.hotger.recipes.utils;

import android.databinding.Observable;
import android.util.Log;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * RealmObject to store the recipe
 */
public class Recipe extends RealmObject {

    @PrimaryKey
    private long id = -1;

    //recipe parts
    private String title; // 0

    private RealmList<Product> products; // 1

    private String preparations; // 4

    private int cookingTime; // 5 (min)

    private int portions; // 6

    private RealmList<RealmString> categories; // 3

    public Recipe() {
        products = new RealmList<>();
        categories = new RealmList<>();
    }

    public void add(Product line) {
        products.add(line);
    }

    public void addAll(RealmList<Product> data) {
        for (Product line : data) {
            products.add(products.size() - 1, line);
        }
    }

    //region Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<Product> getProducts() {
        return products;
    }

    public void setProducts(RealmList<Product> data) {
        this.products = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RealmList<RealmString> getCategories() {
        return categories;
    }

    public void setCategories(RealmList<RealmString> categories) {
        this.categories = categories;
    }

    public String getPreparations() {
        return preparations;
    }

    public void setPreparations(String preparations) {
        this.preparations = preparations;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public String getStringTime(int time) {
        int hours = time / 60;
        int min = time % 60;
        if (hours == 0) {
            return min + " min";
        } else {
            return hours + " : " + min;
        }
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public int getPortions() {
        return portions;
    }

    public String getStringPortions() {
        return String.valueOf(portions);
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }
    //endregion

    public void log() {
        //TODO не сохраняются рецепты (ладно то что фрагмент уходит в onStop это естественно, а вот что он не записывает в объект модели изменения - это уже загадка)
        Log.d("RECIPE_TAG", "---------");
        Log.d("RECIPE_TAG", "title " + title);
        Log.d("RECIPE_TAG", "products " + products.size());
        Log.d("RECIPE_TAG", "preparations " + preparations);
        Log.d("RECIPE_TAG", "cooking time " + cookingTime);
        Log.d("RECIPE_TAG", "portions " + portions);
        Log.d("RECIPE_TAG", "categories " + categories.size());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Recipe r = new Recipe();
        r.setCategories(categories);
        r.setId(id);
        r.setProducts(products);
        r.setCookingTime(cookingTime);
        r.setPortions(portions);
        r.setTitle(title);
        r.setPreparations(preparations);
        return r;
    }

    public boolean hasCategory(String category) {
        for(RealmString string : categories) {
            if (category.equals(string.getName()))
                return true;
        }

        return false;
    }
}

