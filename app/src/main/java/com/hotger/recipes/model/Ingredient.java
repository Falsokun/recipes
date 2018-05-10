package com.hotger.recipes.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Locale;

/**
 * Priority class
 */
@Entity
@IgnoreExtraProperties
public class Ingredient {
    @PrimaryKey
    @NonNull
    private String en;
    private String ru;

    @Ignore
    public Ingredient() {
        en = "";
        ru = "";
    }

    public Ingredient(@NonNull String en, String ru) {
        this.en = en;
        this.ru = ru;
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

    @Ignore
    public String getTitle() {
        if (Locale.getDefault().toString().contains("ru"))
            return ru;
        else
            return en;
    }
    //endregion
}
