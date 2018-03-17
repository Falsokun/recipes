package com.hotger.recipes.utils.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.hotger.recipes.BR;

@Entity
public class Category extends BaseObservable {

    private String url;

    private String title;

    private String type;

    @PrimaryKey
    @NonNull
    private String searchValue;

    @Ignore
    public Category() {}

    public Category(String title, String type, String url, String searchValue) {
        this.title = title;
        this.type = type;
        this.url = url;
        this.searchValue = searchValue;
    }

    //region Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
