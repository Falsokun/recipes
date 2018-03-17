package com.hotger.recipes.utils.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.hotger.recipes.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity(primaryKeys = {"recipeId", "ingredientId"})
public class Product {

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
    private double amount;

    /**
     * Measure of product
     */
    private int measure;

    @Ignore
    public Product() {
    }

    @Ignore
    public Product(String ingredientId) {
        this.ingredientId = ingredientId;
        measure = findMeasureByName(ingredientId);
        amount = 0;
    }

    @Ignore
    public Product(String ingredientId, int amount) {
        this.ingredientId = ingredientId;
        measure = findMeasureByName(ingredientId);
        this.amount = amount;
    }

    @Ignore
    public Product(String ingredientId, int amount, String measure) {
        this.ingredientId = ingredientId;
//        this.measure = measure;
        this.measure = findMeasureByName(measure);
        this.amount = amount;
    }

    public Product(String recipeId, String ingredientId, double amount, int measure) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.amount = amount;
        this.measure = measure;
    }

    //TODO реализовать

    /**
     * Returns the measure variable from the name of the product
     *
     * @param productName - name of the product
     * @return - index
     */
    private int findMeasureByName(Product productName) {
        return Utils.Measure.CUPS;
    }

    private int findMeasureByName(String measure) {
        if (measure == null)
            return Utils.Measure.CUPS;
        switch (measure) {
            case "liter":
                return Utils.Measure.LITERS;
            case "kg":
                return Utils.Measure.KG;
            case "gr":
                return Utils.Measure.GRAMM;
            case "cup":
                return Utils.Measure.CUPS;
            case "tsp":
                return Utils.Measure.TEASPOON;
            case "tbsp":
                return Utils.Measure.TABLESPOON;
            case "pcs":
                return Utils.Measure.PIECE;
        }

        return Utils.Measure.NONE;
    }

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

    public void setMeasure(int measure) {
        this.measure = measure;
    }

    public int getMeasure() {
        return measure;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    //endregion

    //TODO NOW
    public static Product getProductByLine(String productLine) {
        String line = productLine.replaceAll("\\(((\\w+\\s?)+)\\)(\\s|,)?","");
        line = line.toLowerCase();
        line = line.replaceAll("^(,| )+", "");
        Pattern p = Pattern.compile("(\\d+)\\s(?:(cup|stick|pcs|ounce|kg|spoon|tablespoon)\\w?(?:\\s|,)+)?(([A-za-z,]+\\s?)+)");
        Matcher m = p.matcher(line);
        if (!m.matches()) {
            return new Product(line.substring(0, 1).toUpperCase() + line.substring(1));
        }

        String productName = m.group(3).substring(0, 1).toUpperCase() + m.group(3).substring(1);
        return new Product(productName, Integer.parseInt(m.group(1)), m.group(2));
    }

    public String getIngredientById() {
        return ingredientId;
    }
}
