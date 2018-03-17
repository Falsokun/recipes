package com.hotger.recipes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity
public class Fake {
    @PrimaryKey
    @NonNull
    private String en;
    private String ru;
    private String measure;

    public Fake() {
        en = "";
        ru = "";
        measure = "";
    }

    public Fake(String en, String ru, String measure) {
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
    //endregion
}
