package com.hotger.recipes.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.hotger.recipes.database.ListConverter;
import com.hotger.recipes.model.GsonModel.Attributes;
import com.hotger.recipes.model.GsonModel.Image;
import com.hotger.recipes.model.GsonModel.Source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * RealmObject to store the recipe
 */
@Entity
public class RecipeNF implements Serializable {

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

    private String preparations = "";

    @Expose
    private int cookTimeInSeconds = 0;

    @Expose
    private int prepTimeInSeconds = 0;

    @Expose
    private int numberOfServings = 1;

    @Expose
    @TypeConverters({ListConverter.class})
    private List<Image> images = new ArrayList<>();

    @Ignore
    private Attributes attributes = new Attributes();

    @Ignore
    private Source source;

    public RecipeNF() {
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

    public String getPreparations() {
        return preparations;
    }

    public void setPreparations(String preparations) {
        this.preparations = preparations;
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
    //endregion

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
        if (attributes.getCourse() != null)
            out.addAll(attributes.getCourse());

        if (attributes.getCourse() != null)
            out.addAll(attributes.getCuisine());

        if (attributes.getHoliday() != null)
            out.addAll(attributes.getHoliday());
        return out;
    }


//    @Override
//    public Object clone() throws CloneNotSupportedException {
//        RecipeNF r = new RecipeNF();
//        r.setCategories(categories);
//        r.setId(id);
////        r.setProducts(products);
//        r.setTotalTimeInSeconds(totalTimeInSeconds);
//        r.setNumberOfServings(numberOfServings);
//        r.setName(name);
//        r.setPreparations(preparations);
//        return r;
//    }
}

