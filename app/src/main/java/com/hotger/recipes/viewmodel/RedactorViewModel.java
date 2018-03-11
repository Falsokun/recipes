package com.hotger.recipes.viewmodel;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hotger.recipes.R;
import com.hotger.recipes.utils.model.Recipe;
import com.hotger.recipes.view.MainActivity;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;

public class RedactorViewModel extends ViewModel {

    private Activity activity;

    public Recipe currentRecipe;

    private boolean isEdited;

    private InputProductsViewModel inputProductsViewModel;

    public RedactorViewModel(Activity activity) {
        this.activity = activity;
        currentRecipe = new Recipe();
        isEdited = false;
        inputProductsViewModel = new InputProductsViewModel(activity, currentRecipe.getProducts(), true, true);
    }

    public CompoundButton.OnCheckedChangeListener getCheckedListener() {
        return (compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                getCategories().add((String)compoundButton.getTag());
                compoundButton.setBackground(activity.getResources()
                        .getDrawable(R.drawable.chekox_shape_colored));
            } else {
                getCategories().remove(compoundButton.getTag());
                compoundButton.setBackground(activity.getResources()
                        .getDrawable(R.drawable.chekox_shape));
            }
        };
    }
    //endregion

    public ArrayList<String> getCategories() {
        return currentRecipe.getCategories();
    }

    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public InputProductsViewModel getInputProductsViewModel() {
        return inputProductsViewModel;
    }
    //endregion

    public NumberPicker.OnValueChangeListener getPickerChangedListener(boolean isTimePicker, boolean isFirstNumber) {
        return (picker, oldVal, newVal) -> {
            if (isTimePicker) {
                if (isFirstNumber) {
                    currentRecipe.setTotalTimeInSeconds(currentRecipe.getTotalTimeInSeconds() + 60 * (newVal - oldVal));
                } else {
                    currentRecipe.setTotalTimeInSeconds(currentRecipe.getTotalTimeInSeconds() + (newVal - oldVal));
                }
            } else {
                currentRecipe.setNumberOfServings(newVal);
            }
        };
    }

    public void onSave(ViewPager viewPager) {
//        currentRecipe.setProducts(productsAdapter.getData());
        if (!isDataCorrect(viewPager)) {
            return;
        }

//        activity.getRealmInstance().beginTransaction();
//        if (currentRecipe.getId() == -1) {
//            int newId = activity.getRealmInstance().where(Recipe.class).findAllSorted("id", Sort.DESCENDING).size();
//            currentRecipe.setId(newId + 1);
//        }
//
//        activity.getRealmInstance().copyToRealmOrUpdate(currentRecipe);
//        activity.getRealmInstance().commitTransaction();
//        activity.updateAdapter();
    }

    public boolean isDataCorrect(ViewPager viewPager) {
        int errorType = -1;
        if (currentRecipe.getName() == null) {
            Toast.makeText(activity,
                    activity.getResources().getString(R.string.fill_data), Toast.LENGTH_SHORT).show();
            return false;
        } else if (currentRecipe.getProducts().size() == 0) {
            errorType = 0;
        } else if (currentRecipe.getPreparations() == null) {
            errorType = 1;
        } else if (currentRecipe.getTotalTimeInSeconds() == 0) {
            errorType = 2;
        } else if (currentRecipe.getNumberOfServings() == 0) {
            errorType = 3;
        }
        if (errorType > -1) {
            Toast.makeText(activity,
                    activity.getResources().getString(R.string.fill_data), Toast.LENGTH_SHORT).show();
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

    public boolean isEdited() {
        return isEdited;
    }
}
