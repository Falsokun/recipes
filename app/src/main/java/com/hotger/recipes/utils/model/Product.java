package com.hotger.recipes.utils.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hotger.recipes.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.RealmObject;

//TODO: МНОГО ЧЕГО СДЕЛАТЬ
public class Product extends RealmObject {

    /**
     * Product name
     */
    @SerializedName("searchValue")
    @Expose
    private String productName;

    /**
     * Amount of product
     */
    private double amount;

    /**
     * Measure of product
     */
    private int measure;

    public Product() {
    }

    public Product(String productName) {
        this.productName = productName;
        measure = findMeasureByName(productName);
        amount = 0;
    }

    public Product(String productName, int amount) {
        this.productName = productName;
        measure = findMeasureByName(productName);
        this.amount = amount;
    }

    public Product(String productName, int amount, String measure) {
        this.productName = productName;
//        this.measure = measure;
        this.measure = findMeasureByName(measure);
        this.amount = amount;
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
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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
    //endregion

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
}
