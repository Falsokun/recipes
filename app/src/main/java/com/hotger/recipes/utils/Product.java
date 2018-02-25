package com.hotger.recipes.utils;

import io.realm.RealmObject;

public class Product extends RealmObject {

    /**
     * Product name
     */
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

    //TODO реализовать
    /**
     * Returns the measure variable from the name of the product
     *
     * @param productName - name of the product
     * @return - index
     */
    private int findMeasureByName(String productName) {
        return Utils.Measure.CUPS;
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
}
