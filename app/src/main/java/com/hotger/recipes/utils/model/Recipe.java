package com.hotger.recipes.utils.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * RealmObject to store the recipe
 */
@Entity(
        foreignKeys = @ForeignKey(entity = Recipe.ImageURL.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE),
        primaryKeys = {"recipeId"})
public class Recipe implements Serializable {

    @NonNull
    @PrimaryKey
    @Expose
    private String id;

    //recipe parts
    @Expose
    private String name;

    @Expose
    @Ignore
    private ArrayList<String> ingredientLines;

    private ArrayList<Product> products;

    private String preparations;

    @Expose
    private int totalTimeInSeconds;

    @Expose
    private int numberOfServings;

    @Expose
    private ArrayList<ImageURL> images;

    private ArrayList<String> categories;

    private boolean isFromAPI = true;

    public Recipe() {
        products = new ArrayList<>();
        categories = new ArrayList<>();
    }

    public void add(Product line) {
        products.add(line);
    }

    //region Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> data) {
        this.products = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public String getPreparations() {
        return preparations;
    }

    public void setPreparations(String preparations) {
        this.preparations = preparations;
    }

    public int getTotalTimeInSeconds() {
        return totalTimeInSeconds;
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

    public void setTotalTimeInSeconds(int totalTimeInSeconds) {
        this.totalTimeInSeconds = totalTimeInSeconds;
    }

    public int getNumberOfServings() {
        return numberOfServings;
    }

    public String getStringPortions() {
        return String.valueOf(numberOfServings);
    }

    public void setNumberOfServings(int numberOfServings) {
        this.numberOfServings = numberOfServings;
    }

    public ArrayList<String> getIngredientLines() {
        return ingredientLines;
    }

    public void setIngredientLines(ArrayList<String> ingredientLines) {
        this.ingredientLines = ingredientLines;
    }

    public boolean isFromAPI() {
        return isFromAPI;
    }

    public void setFromAPI(boolean fromAPI) {
        isFromAPI = fromAPI;
    }
    //endregion

    public void log() {
        //TODO не сохраняются рецепты (ладно то что фрагмент уходит в onStop это естественно, а вот что он не записывает в объект модели изменения - это уже загадка)
        Log.d("RECIPE_TAG", "---------");
        Log.d("RECIPE_TAG", "name " + name);
        Log.d("RECIPE_TAG", "products " + products.size());
        Log.d("RECIPE_TAG", "preparations " + preparations);
        Log.d("RECIPE_TAG", "cooking time " + totalTimeInSeconds);
        Log.d("RECIPE_TAG", "numberOfServings " + numberOfServings);
        Log.d("RECIPE_TAG", "categories " + categories.size());
    }

    public void prepareDataForShowing() {
        if (isFromAPI) {
            totalTimeInSeconds /= 60;
            for (String ingredientLine : ingredientLines) {
                products.add(Product.getProductByLine(ingredientLine));
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Recipe r = new Recipe();
        r.setCategories(categories);
        r.setId(id);
        r.setProducts(products);
        r.setTotalTimeInSeconds(totalTimeInSeconds);
        r.setNumberOfServings(numberOfServings);
        r.setName(name);
        r.setPreparations(preparations);
        return r;
    }

    public boolean hasCategory(String category) {
        return categories.contains(category);
    }

    public String getImageURL() {
        return images.get(0).getHostedLargeUrl();
    }

    @Entity
    public class ImageURL implements Serializable {

        @PrimaryKey
        @NonNull
        private String recipeId;

        public ImageURL(String recipeId){
            this.recipeId = recipeId;
        }

        @SerializedName("hostedSmallUrl")
        @Expose
        private String hostedSmallUrl;

        @SerializedName("hostedMediumUrl")
        @Expose
        private String hostedMediumUrl;

        @SerializedName("hostedLargeUrl")
        @Expose
        private String hostedLargeUrl;

        //region Getters and Setters
        public String getHostedSmallUrl() {
            return hostedSmallUrl;
        }

        public void setHostedSmallUrl(String hostedSmallUrl) {
            this.hostedSmallUrl = hostedSmallUrl;
        }

        public String getHostedMediumUrl() {
            return hostedMediumUrl;
        }

        public void setHostedMediumUrl(String hostedMediumUrl) {
            this.hostedMediumUrl = hostedMediumUrl;
        }

        public String getHostedLargeUrl() {
            return hostedLargeUrl;
        }

        public void setHostedLargeUrl(String hostedLargeUrl) {
            this.hostedLargeUrl = hostedLargeUrl;
        }

        @NonNull
        public String getRecipeId() {
            return recipeId;
        }

        public void setRecipeId(@NonNull String recipeId) {
            this.recipeId = recipeId;
        }

        //endregion
    }
}

