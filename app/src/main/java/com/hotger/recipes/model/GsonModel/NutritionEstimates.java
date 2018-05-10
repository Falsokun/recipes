package com.hotger.recipes.model.GsonModel;

/**
 * Class to get response from {@link com.hotger.recipes.utils.YummlyAPI} by gson
 */
public class NutritionEstimates {
    private String attribute;

    private String description;

    private String value;

    public NutritionEstimates() {}

    public NutritionEstimates(String attribute, String value, String description) {
        this.attribute = attribute;
        this.value = value;
        this.description = description;
    }

    //region Getters and setters
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description == null ? attribute : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //endregion
}
