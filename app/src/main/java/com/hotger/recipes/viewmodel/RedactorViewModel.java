package com.hotger.recipes.viewmodel;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hotger.recipes.R;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.utils.RecipeUtils;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.ControllableActivity;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;

public class RedactorViewModel extends ViewModel {

    private ControllableActivity activity;

    private Recipe currentRecipe;

    private boolean isEdited;

    private InputProductsViewModel inputProductsViewModel;

    private ArrayList<String> categoryTitles = new ArrayList<>();

    public RedactorViewModel(ControllableActivity activity) {
        this.activity = activity;
        currentRecipe = new Recipe();
        isEdited = false;
        inputProductsViewModel = new InputProductsViewModel(activity, currentRecipe.getProducts(), true, true, false);
    }

    public RedactorViewModel(ControllableActivity activity, String id) {
        this.activity = activity;
        currentRecipe = activity.getRecipeFromDBByID(id);
        isEdited = false;
        inputProductsViewModel = new InputProductsViewModel(activity, currentRecipe.getProducts(), true, true, false);
        for(Category category : currentRecipe.getCategories()) {
            categoryTitles.add(category.getTitle());
        }
    }

    //region listeners
    public CompoundButton.OnCheckedChangeListener getCheckedListener() {
        return (compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                categoryTitles.add((String) compoundButton.getTag());
                compoundButton.setBackground(activity.getResources()
                        .getDrawable(R.drawable.chekox_shape_colored));
            } else {
                categoryTitles.remove(compoundButton.getTag());
                compoundButton.setBackground(activity.getResources()
                        .getDrawable(R.drawable.chekox_shape));
            }
        };
    }

    public NumberPicker.OnValueChangeListener getPickerChangedListener() {
        return (picker, oldVal, newVal) ->
                currentRecipe.setNumberOfServings(newVal);
    }
    //endregion

    /**
     * Returns true if data correct
     * @param viewPager
     * @return
     */
    public boolean onSave(ViewPager viewPager) {
        if (!isDataCorrect(viewPager)) {
            return false;
        }

        RecipeUtils.saveToDatabase(currentRecipe, activity, true, categoryTitles, Utils.TYPE.TYPE_MY_RECIPES, false);
        return true;
    }

    public boolean isDataCorrect(ViewPager viewPager) {
        String errorText = "";
        int errorType = -1;
        if (currentRecipe.getName() == null) {
            Toast.makeText(activity,
                    activity.getResources().getString(R.string.fill_data) + activity.getString(R.string.recipe_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (currentRecipe.getProducts().size() == 0) {
            errorText = activity.getString(R.string.add_products);
            errorType = 0;
        } else if (currentRecipe.getPreparations() == null) {
            errorText = activity.getString(R.string.instructions);
            errorType = 2;
        } else if (currentRecipe.getTotalTimeInMinutes() == 0) {
            errorText = activity.getString(R.string.total_time);
            errorType = 4;
        }

        if (errorText.length() != 0) {
            Toast.makeText(activity,
                    activity.getResources().getString(R.string.fill_data) + errorText, Toast.LENGTH_SHORT).show();
            viewPager.setCurrentItem(errorType);
            return false;
        }

        return true;
    }

    @Override
    public void OnResume() {

    }

    @Override
    public void OnPause() {

    }

    //region getters setters
    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public InputProductsViewModel getInputProductsViewModel() {
        return inputProductsViewModel;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        this.isEdited = edited;
    }
    //endregion
}
