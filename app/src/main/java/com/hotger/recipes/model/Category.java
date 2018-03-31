package com.hotger.recipes.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.hotger.recipes.BR;

import java.io.Serializable;
import java.util.Locale;

@Entity
public class Category extends BaseObservable implements Serializable {

    @PrimaryKey
    @NonNull
    private String searchValue;

    private String url;

    private String enTitle;

    private String ruTitle;

    private String type;

    @Ignore
    public Category() {
    }

    public Category(String url, String enTitle, String ruTitle, String type, @NonNull String searchValue) {
        this.url = url;
        this.enTitle = enTitle;
        this.ruTitle = ruTitle;
        this.type = type;
        this.searchValue = searchValue;
    }

    //region Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEnTitle() {
        return enTitle;
    }

    public void setEnTitle(String enTitle) {
        this.enTitle = enTitle;
        notifyPropertyChanged(BR.title);
    }

    public String getRuTitle() {
        return ruTitle;
    }

    public void setRuTitle(String ruTitle) {
        this.ruTitle = ruTitle;
        notifyPropertyChanged(BR.title);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
    //endregion

    @Bindable
    public String getTitle() {
        if (Locale.getDefault().toString().contains("ru")) {
            return ruTitle;
        } else {
            return enTitle;
        }
    }
}
