package com.hotger.recipes.model.GsonModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Attributes implements Serializable {

    private ArrayList<String> course;

    private ArrayList<String> holiday;

    private ArrayList<String> cuisine;

    //region Getters and Setters
    public ArrayList<String> getCourse() {
        return course;
    }

    public void setCourse(ArrayList<String> course) {
        this.course = course;
    }

    public ArrayList<String> getHoliday() {
        return holiday;
    }

    public void setHoliday(ArrayList<String> holiday) {
        this.holiday = holiday;
    }

    public ArrayList<String> getCuisine() {
        return cuisine;
    }

    public void setCuisine(ArrayList<String> cuisine) {
        this.cuisine = cuisine;
    }
    //endregion
}
