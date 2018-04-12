package com.hotger.recipes;

import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

public class IngredientFake {
    @PrimaryKey
    @NonNull
    private String id;
    private String en;
    private String ru;

    public IngredientFake() {}

    public IngredientFake(@NonNull String id, String en, String ru) {
        this.id = id;
        this.en = en;
        this.ru = ru;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

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
}
