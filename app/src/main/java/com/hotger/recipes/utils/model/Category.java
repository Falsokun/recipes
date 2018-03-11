package com.hotger.recipes.utils.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import com.hotger.recipes.BR;

@Entity
public class Category extends BaseObservable {

    private String imageURL;

    private String title;

    private String description;

    @PrimaryKey
    @NonNull
    private String searchValue;

    public Category(String title, String description, String imageURL, String searchValue) {
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.searchValue = searchValue;
    }

    //region Getters and Setters
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
    //endregion
}
