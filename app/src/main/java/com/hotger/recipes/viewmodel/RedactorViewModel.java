package com.hotger.recipes.viewmodel;

import android.app.Activity;
import android.databinding.Bindable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hotger.recipes.BR;
import com.hotger.recipes.R;
import com.hotger.recipes.adapter.DataHintAdapter;
import com.hotger.recipes.adapter.ProductsAdapter;
import com.hotger.recipes.utils.Product;
import com.hotger.recipes.utils.RealmString;
import com.hotger.recipes.utils.Recipe;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.MainActivity;
import com.shawnlin.numberpicker.NumberPicker;

import io.realm.RealmList;
import io.realm.Sort;

public class RedactorViewModel extends ViewModel {

    private MainActivity activity;

    private ProductsAdapter productsAdapter;

    public Recipe currentRecipe;

    private boolean isKeyboardVisible = false;

    private DataHintAdapter adapter;

    private String productName;

    private boolean isEdited;

    public RedactorViewModel(MainActivity activity) {
        this.activity = activity;
        adapter = new DataHintAdapter(activity, R.layout.item_list);
        currentRecipe = new Recipe();
        productsAdapter = new ProductsAdapter(activity, currentRecipe.getProducts(), true);
        isEdited = false;
    }

    public RedactorViewModel(MainActivity activity, int recipeId) {
        this.activity = activity;
        adapter = new DataHintAdapter(activity, R.layout.item_list);
        Recipe recipe = activity
                .getRealmInstance()
                .where(Recipe.class)
                .equalTo("id", recipeId)
                .findAll()
                .get(0);
        try {
            currentRecipe = (Recipe) recipe.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            currentRecipe = new Recipe();
        }
        productsAdapter = new ProductsAdapter(activity, currentRecipe.getProducts(), true);
        isEdited = true;
    }

    //region Listeners
    public View.OnFocusChangeListener getOnFocusChangedListener() {
        return (view, hasFocus) -> {
            if (!hasFocus) {
                Utils.hideKeyboard(view);
            }

            setKeyboardVisible(hasFocus);
        };
    }

    public TextWatcher getProductTextChangeListener(final ArrayAdapter<String> adapter) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }

    public void onHintItemClickAction(View view, int i, long l) {
        TextView childView = view.findViewById(R.id.product_name);
        String productName = childView.getText().toString();
        if (!productsAdapter.isAlreadyInSet(productName)) {
            productsAdapter.addData(new Product(productName));
        } else {
            Toast.makeText(activity, "Already in list", Toast.LENGTH_LONG).show();
        }
    }

    public CompoundButton.OnCheckedChangeListener getCheckedListener() {
        return (compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                getCategories().add(new RealmString((String) compoundButton.getTag()));
                compoundButton.setBackground(activity.getResources()
                        .getDrawable(R.drawable.chekox_shape_colored));
            } else {
                getCategories().remove(new RealmString((String) compoundButton.getTag()));
                compoundButton.setBackground(activity.getResources()
                        .getDrawable(R.drawable.chekox_shape));
            }
        };
    }
    //endregion

    //region Getters and Setters
    @Bindable
    public boolean isKeyboardVisible() {
        return isKeyboardVisible;
    }

    @Bindable
    public void setKeyboardVisible(boolean keyboardVisible) {
        isKeyboardVisible = keyboardVisible;
        notifyPropertyChanged(BR.keyboardVisible);
    }

    public ProductsAdapter getProductsAdapter() {
        return productsAdapter;
    }

    public RealmList<RealmString> getCategories() {
        return currentRecipe.getCategories();
    }

    @Bindable
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
        notifyPropertyChanged(BR.productName);
    }

    public DataHintAdapter getAdapter() {
        return adapter;
    }

    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    //endregion

    public NumberPicker.OnValueChangeListener getPickerChangedListener(boolean isTimePicker, boolean isFirstNumber) {
        return (picker, oldVal, newVal) -> {
            if (isTimePicker) {
                if (isFirstNumber) {
                    currentRecipe.setCookingTime(currentRecipe.getCookingTime() + 60 * (newVal - oldVal));
                } else {
                    currentRecipe.setCookingTime(currentRecipe.getCookingTime() + (newVal - oldVal));
                }
            } else {
                currentRecipe.setPortions(newVal);
            }
        };
    }

    public void onSave(ViewPager viewPager) {
//        currentRecipe.setProducts(productsAdapter.getData());
        if (!isDataCorrect(viewPager)) {
            return;
        }

        activity.getRealmInstance().beginTransaction();
        if (currentRecipe.getId() == -1) {
            int newId = activity.getRealmInstance().where(Recipe.class).findAllSorted("id", Sort.DESCENDING).size();
            currentRecipe.setId(newId + 1);
        }

        activity.getRealmInstance().copyToRealmOrUpdate(currentRecipe);
        activity.getRealmInstance().commitTransaction();
        activity.updateAdapter();
    }

    public boolean isDataCorrect(ViewPager viewPager) {
        int errorType = -1;
        if (currentRecipe.getTitle() == null) {
            Toast.makeText(activity,
                    activity.getResources().getString(R.string.fill_data), Toast.LENGTH_SHORT).show();
            return false;
        } else if (currentRecipe.getProducts().size() == 0) {
            errorType = 0;
        } else if (currentRecipe.getPreparations() == null) {
            errorType = 1;
        } else if (currentRecipe.getCookingTime() == 0) {
            errorType = 2;
        } else if (currentRecipe.getPortions() == 0) {
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
