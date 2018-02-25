package com.hotger.recipes.utils;

import io.realm.RealmObject;

/**
 * Shell to create RealmList<String>
 */
public class RealmString extends RealmObject {

    private String name;

    public RealmString() {
        name = "";
    }

    public RealmString(String name) {
        this.name = name;
    }

    //region Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //endregion


    @Override
    public String toString() {
        return name;
    }
}
