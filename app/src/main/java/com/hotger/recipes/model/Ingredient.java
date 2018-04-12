package com.hotger.recipes.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Locale;

@Entity
@IgnoreExtraProperties
public class Ingredient {
    @PrimaryKey
    @NonNull
    private String en;
    private String ru;
    private String measure;

    @Ignore
    public Ingredient() {
        en = "";
        ru = "";
        measure = "";
    }

    public Ingredient(@NonNull String en, String ru, String measure) {
        this.en = en;
        this.ru = ru;
        this.measure = measure;
    }

    //region Getters and setters
    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    @Ignore
    public String getTitle() {
        if (Locale.getDefault().toString().contains("ru"))
            return ru;
        else
            return en;
    }
    //endregion
}
