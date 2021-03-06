package com.hotger.recipes.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;
import android.content.Context;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.hotger.recipes.R;
import com.hotger.recipes.database.FirebaseUtils;
import com.hotger.recipes.model.GsonModel.Image;
import com.hotger.recipes.model.GsonModel.NutritionEstimates;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.view.ControllableActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Priority class
 */
@IgnoreExtraProperties
public class Recipe implements Serializable {

    @Embedded
    public ApiRecipe recipe;

    @Relation(parentColumn = "id", entity = Product.class, entityColumn = "recipeId")
    private List<Product> products = new ArrayList<>();

    @Ignore
    private List<Category> categories = new ArrayList<>();

    public void add(Product line) {
        products.add(line);
    }

    @Ignore
    public Recipe() {
        recipe = new ApiRecipe();
    }

    public Recipe(ApiRecipe recipe, ControllableActivity activity) {
        this.recipe = recipe;
        filterCategories(activity, recipe.getAllAttributes());
    }

    public Recipe(ApiRecipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Get all categories from database by its name
     *
     * @param activity      - context
     * @param allAttributes - category names
     */
    private void filterCategories(ControllableActivity activity, ArrayList<String> allAttributes) {
        for (String category : allAttributes) {
            List<Category> queryRes = AppDatabase.getDatabase(activity).getCategoryDao().getCategoryByName(category);
            if (queryRes.size() != 0) {
                Category cat = queryRes.get(0);
                if (cat != null) {
                    categories.add(cat);
                }
            }
        }
    }

    /**
     * Create image object from url and use it as a setter
     *
     * @param url - image url
     */
    public void setImageURL(String url) {
        Image im = new Image(url);
        ArrayList<Image> list = new ArrayList<>();
        list.add(im);
        recipe.setImages(list);
    }

    /**
     * Get image url from {@link Image} object
     *
     * @return
     */
    public String getImageURL() {
        if (recipe.getImages().size() == 0) {
            return FirebaseUtils.NO_IMAGE_URL;
        }

        return recipe.getImages().get(0).getUrl();
    }

    /**
     * Checks if {@param category} contains in current categories
     *
     * @param category
     * @return <code>true</code> if contains
     */
    public boolean hasCategory(String category) {
        return getCategoriesTitles().contains(category);
    }

    public String asString(Context context) {
        StringBuffer stringBuffer = new StringBuffer();
        String newLine = "\n";
        stringBuffer.append(context.getString(R.string.app_name) + " app: ");
        stringBuffer.append(newLine);
        stringBuffer.append(newLine);
        stringBuffer.append(recipe.getName().toUpperCase());
        stringBuffer.append(newLine);
        stringBuffer.append(context.getString(R.string.total_time) + ": " + (recipe.getTotalTimeInSeconds() / 60)
                + " " + context.getString(R.string.min));
        stringBuffer.append(newLine);
        stringBuffer.append(context.getString(R.string.prep_time) + ": " + recipe.getPrepTimeInMinutes()
                + " " + context.getString(R.string.min));
        stringBuffer.append(newLine);
        stringBuffer.append(context.getString(R.string.cooking_time) + ": " + recipe.getCookTimeInMinutes()
                + " " + context.getString(R.string.min));
        stringBuffer.append(newLine);
        stringBuffer.append(context.getString(R.string.persons_number) + ": " + recipe.getStringPortions());
        stringBuffer.append(newLine);
        stringBuffer.append(context.getString(R.string.number_of_calories) + ": " + recipe.getCalories());
        stringBuffer.append(newLine);
        stringBuffer.append(context.getString(R.string.ingredients) + ": ");
        stringBuffer.append(newLine);
        for (Product p : products) {
            stringBuffer.append("- " + p.asString(context));
            stringBuffer.append(newLine);
        }

        stringBuffer.append(recipe.getInstructions());

        stringBuffer.append(newLine);
        for (Category category : categories) {
            stringBuffer.append("#" + category.getTitle() + " ");
        }

        if (recipe.getImageUrl() != null && !recipe.getImageUrl().equals(FirebaseUtils.NO_IMAGE_URL)) {
            stringBuffer.append(recipe.getImageUrl());
        }

        return stringBuffer.toString();
    }

    //region getters and setters
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> data) {
        this.products = data;
    }

    public ApiRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(ApiRecipe recipe) {
        this.recipe = recipe;
    }

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
        for (Category category : categories) {
            titles.add(category.getTitle());
        }

        return titles;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getPreparations() {
        return recipe.getInstructions();
    }

    public void setPreparations(String preparations) {
        recipe.setInstructions(preparations);

    }

    @Ignore
    public int getCookingTimeInMinutes() {
        return recipe.getCookTimeInMinutes();
    }

    @Ignore
    public int getPrepTimeMinutes() {
        return recipe.getPrepTimeInMinutes();
    }

    public void setCookTimeInMinutes(int cookTimeInMin) {
        recipe.setCookTimeInSeconds(cookTimeInMin * 60);
    }

    public void setPrepTimeInMinutes(int prepTimeInMin) {
        recipe.setPrepTimeInSeconds(prepTimeInMin * 60);
    }

    public void setCookTimeInSeconds(int cookTimeInSeconds) {
        recipe.setCookTimeInSeconds(cookTimeInSeconds);
    }

    public void setPrepTimeInSeconds(int prepTimeInSeconds) {
        recipe.setPrepTimeInSeconds(prepTimeInSeconds);
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

    public void setLang(String lang) {
        recipe.setLang(lang);
    }

    public String getLang() {
        return recipe.getLang();
    }

    public void setCalories(int calories) {
        this.recipe.setCalories(calories);
    }

    public int getCalories() {
        return this.recipe.getCalories();
    }

    public List<NutritionEstimates> getNutritionEstimates() {
        return recipe.getNutritionEstimates();
    }
    //endregion
}