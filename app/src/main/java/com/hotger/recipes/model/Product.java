package com.hotger.recipes.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.hotger.recipes.view.ControllableActivity;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity(
        foreignKeys = @ForeignKey(entity = RecipeNF.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE),
        primaryKeys = {"recipeId", "ingredientId"})
public class Product implements Serializable {

    @NonNull
    private String recipeId;
    /**
     * Product name
     */
//    @SerializedName("searchValue")
//    @Expose
    @NonNull
    private String ingredientId;

    /**
     * Amount of product
     */
    private double amount = 0;

    //TODO measure нужно из базы качать
    /**
     * Measure of product
     */
    private String measure;

    @Ignore
    public Product() {
    }

    @Ignore
    public Product(String name, ControllableActivity activity) {
        findParamsInDB(activity, name);
    }

    @Ignore
    public Product(ControllableActivity activity, String ingredientId, int amount) {
        this.ingredientId = ingredientId;
        measure = findMeasureByName(activity, ingredientId);
        this.amount = amount;
    }

    @Ignore
    public Product(ControllableActivity activity, String ingredientId, int amount, String measure) {
        this.ingredientId = ingredientId;
        this.measure = measure;
        this.amount = amount;
    }

    public Product(String recipeId, String ingredientId, double amount, String measure) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.amount = amount;
        this.measure = measure;
    }

    //TODO реализовать

    //region Getters and Setters
    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getMeasure() {
        return measure;
    }

    public int getDrawableByMeasure() {
        return 0;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    //endregion

    //TODO может тут лучше не парсинг, а посмотреть по базе чо есть (нооо это долго наверное)
    public static Product getProductByLine(String productLine, ControllableActivity activity) {
        String line = productLine.replaceAll("\\(((\\w+\\s?)+)\\)(\\s|,)?","");
        line = line.toLowerCase();
        line = line.replaceAll("^(,| )+", "");
        Pattern p = Pattern.compile("(\\d+)\\s(?:(cup|stick|pcs|ounce|kg|spoon|tablespoon)\\w?(?:\\s|,)+)?(([A-za-z,]+\\s?)+)");
        Matcher m = p.matcher(line);
        if (!m.matches()) {
            return new Product(line.substring(0, 1).toUpperCase() + line.substring(1), activity);
        }

        String amount = m.group(1);
        String measure = m.group(2);
        String productName = m.group(3).substring(0, 1).toUpperCase() + m.group(3).substring(1);
        return new Product(activity, productName, Integer.parseInt(amount), measure);
    }

    public void findParamsInDB(ControllableActivity activity, String name) {
        List<Ingredient> ingredients = activity.getDatabase().getIngredientDao().getIngredientByName(name);
        if (ingredients.size() == 0) {
            ingredientId = name;
            measure = "cup";
            return;
        }

        Ingredient ingredient = activity.getDatabase().getIngredientDao().getIngredientByName(name).get(0);
        ingredientId = ingredient.getId();
        measure = ingredient.getMeasure();
    }

    public String findMeasureByName(ControllableActivity activity, String name) {
        List<Ingredient> ingredients = activity.getDatabase().getIngredientDao().getIngredientByName(name);
        if (ingredients.size() == 0)
            return "lt";

        return ingredients.get(0).getMeasure();
    }

    public String getIngredientById(ControllableActivity activity) {
        List<Ingredient> ingredients = activity
                .getDatabase()
                .getIngredientDao()
                .getIngredientById(ingredientId);

        if (ingredients.size() != 0) {
            return ingredients.get(0).getEn();
        }

        return ingredientId;
    }
}
