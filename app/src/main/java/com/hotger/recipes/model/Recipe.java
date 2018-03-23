package com.hotger.recipes.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;
import android.databinding.BaseObservable;

import com.hotger.recipes.model.GsonModel.Image;
import com.hotger.recipes.view.ControllableActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Serializable{

    @Embedded
    public RecipeNF recipe;

    @Relation(parentColumn = "id", entity = Product.class, entityColumn = "recipeId")
    private List<Product> products = new ArrayList<>();

    @Ignore
    private List<Category> categories = new ArrayList<>();

    public void add(Product line) {
        products.add(line);
    }

    @Ignore
    public Recipe() {
        recipe = new RecipeNF();
    }

    public Recipe(RecipeNF recipe, ControllableActivity activity) {
        this.recipe = recipe;
        filterCategories(activity, recipe.getAllAttributes());
    }

    public Recipe(RecipeNF recipe) {
        this.recipe = recipe;
    }

    private void filterCategories(ControllableActivity activity, ArrayList<String> allAttributes) {
        for(String category : allAttributes) {
            List<Category> queryRes = activity.getDatabase().getCategoryDao().getCategoryByName(category);
            if (queryRes.size() != 0) {
                Category cat = queryRes.get(0);
                if (cat != null) {
                    categories.add(cat);
                }
            }
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> data) {
        this.products = data;
    }

    public RecipeNF getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeNF recipe) {
        this.recipe = recipe;
    }

    public void prepareDataForShowing(ControllableActivity activity) {
        for (String ingredientLine : recipe.getIngredientLines()) {
            products.add(Product.getProductByLine(ingredientLine, activity));
        }
    }

    //region simplier life
    public String getName() {
        return recipe.getName();
    }

    public void setName(String name) {
        recipe.setName(name);
    }

    public String getId() {
        return recipe.getId();
    }

    public void setId(String id) {
        recipe.setId(id);
    }

    public List<Category> getCategories() {
        return categories;
    }

    public ArrayList<String> getCategoriesTitles() {
        ArrayList<String> titles = new ArrayList<>();
        for(Category category : categories) {
            titles.add(category.getTitle());
        }

        return titles;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public String getPreparations() {
        return recipe.getPreparations();
    }

    public void setPreparations(String preparations) {
        recipe.setPreparations(preparations);
    }

    public int getCookingTimeInMinutes() {
        return recipe.getCookTimeInMinutes();
    }

    public int getPrepTimeMinutes() {
        return recipe.getPrepTimeInMinutes();
    }

    public void setCookTimeInSeconds(int cookTimeInSeconds) {
        recipe.setCookTimeInSeconds(cookTimeInSeconds);
    }

    public void setPrepTimeInSeconds(int prepTimeInSeconds) {
        recipe.setPrepTimeInSeconds(prepTimeInSeconds);
    }

    public void setCookTimeInMinutes(int cookTimeInMin) {
        recipe.setCookTimeInSeconds(cookTimeInMin * 60);
    }

    public void setPrepTimeInMinutes(int prepTimeInMin) {
        recipe.setPrepTimeInSeconds(prepTimeInMin * 60);
    }

    public int getTotalTimeInMinutes() {
        return recipe.getTotalTimeInSeconds() / 60;
    }

    public int getNumberOfServings() {
        return recipe.getNumberOfServings();
    }

    public String getStringPortions() {
        return String.valueOf(recipe.getNumberOfServings());
    }

    public void setNumberOfServings(int numberOfServings) {
        recipe.setNumberOfServings(numberOfServings);
    }

    public ArrayList<String> getIngredientLines() {
        return recipe.getIngredientLines();
    }

    public void setIngredientLines(ArrayList<String> ingredientLines) {
        recipe.setIngredientLines(ingredientLines);
    }
    //endregion

    public void setImageURL(String url) {
        Image im = new Image(url);
        ArrayList<Image> list = new ArrayList<>();
        list.add(im);
        recipe.setImages(list);
    }

    public String getImageURL() {
        return recipe.getImages().get(0).getUrl();
    }

    public boolean hasCategory(String category) {
        return getCategoriesTitles().contains(category);
    }

//    private JSONObject toJSON() {
//
//    }
}