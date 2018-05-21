package com.hotger.recipes.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.hotger.recipes.database.ObjConverter;
import com.hotger.recipes.model.GsonModel.Attributes;
import com.hotger.recipes.model.GsonModel.Image;
import com.hotger.recipes.model.GsonModel.NutritionEstimates;
import com.hotger.recipes.model.GsonModel.Source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ApiRecipe implements Serializable {

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

    private String instructions = "";

    @Expose
    private int cookTimeInSeconds = 0;

    @Expose
    private int prepTimeInSeconds = 0;

    @Expose
    private int numberOfServings = 1;

    @Expose
    @TypeConverters({ObjConverter.class})
    private List<Image> images = new ArrayList<>();

    @Ignore
    private Attributes attributes = new Attributes();

    @Expose
    @TypeConverters({ObjConverter.class})
    private List<NutritionEstimates> nutritionEstimates = new ArrayList<>();

    @TypeConverters({ObjConverter.class})
    private Source source = new Source();

    private String lang = "en";

    private int calories;

    public ApiRecipe() {
    }

    public int getCookTimeInMinutes() {
        return cookTimeInSeconds / 60;
    }

    public int getPrepTimeInMinutes() {
        return prepTimeInSeconds / 60;
    }

    public String getImageUrl() {
        if (images == null
                || images.size() == 0
                || images.get(0) == null)
            return null;
        return images.get(0).getUrl();
    }

    public ArrayList<String> getAllAttributes() {
        ArrayList<String> out = new ArrayList<>();
        if (attributes == null) {
            return out;
        }

        if (attributes.getCourse() != null)
            out.addAll(attributes.getCourse());

        if (attributes.getCuisine() != null)
            out.addAll(attributes.getCuisine());

        if (attributes.getHoliday() != null)
            out.addAll(attributes.getHoliday());
        return out;
    }

    //region Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getCookTimeInSeconds() {
        return cookTimeInSeconds;
    }

    public int getPrepTimeInSeconds() {
        return prepTimeInSeconds;
    }

    public void setCookTimeInSeconds(int cookTimeInSeconds) {
        this.cookTimeInSeconds = cookTimeInSeconds;
    }

    public void setPrepTimeInSeconds(int prepTimeInSeconds) {
        this.prepTimeInSeconds = prepTimeInSeconds;
    }

    public int getTotalTimeInSeconds() {
        return prepTimeInSeconds + cookTimeInSeconds;
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public List<NutritionEstimates> getNutritionEstimates() {
        return nutritionEstimates;
    }

    public void setNutritionEstimates(List<NutritionEstimates> nutritionEstimates) {
        this.nutritionEstimates = nutritionEstimates;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    //endregion
}

