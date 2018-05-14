package com.hotger.recipes.viewmodel;


import android.content.Context;
import android.databinding.Bindable;
import android.util.Rational;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;

import com.hotger.recipes.BR;
import com.hotger.recipes.R;
import com.hotger.recipes.adapter.ProductsAdapter;
import com.hotger.recipes.model.Ingredient;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.MeasureUtils;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.ControllableActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeViewModel extends ViewModel {

    private Recipe mCurrentRecipe;
    private ControllableActivity activity;
    private ProductsAdapter productsAdapter;

    public RecipeViewModel(ControllableActivity activity) {
        this.activity = activity;
        mCurrentRecipe = null;
    }

    @Bindable
    public Recipe getCurrentRecipe() {
        return mCurrentRecipe;
    }

    public void setCurrentRecipe(Recipe mCurrentRecipe) {
        this.mCurrentRecipe = mCurrentRecipe;
        productsAdapter = new ProductsAdapter(activity, mCurrentRecipe.getProducts(), false, true, false);
        notifyPropertyChanged(BR.currentRecipe);
    }

    public void setInstructions(String instructions) {
        mCurrentRecipe.setPreparations(instructions);
        notifyPropertyChanged(BR.currentRecipe);
    }

    @Override
    public void OnResume() {
        activity.setUpNavigation(true);
    }

    @Override
    public void OnPause() {

    }

    public ProductsAdapter getProductsAdapter() {
        return productsAdapter;
    }

    public void addCategories(ViewGroup categoryContainer) {
        ArrayList<String> categories = getCurrentRecipe().getCategoriesTitles();
        for (int i = 0; i < categories.size(); i++) {
            TableRow row = new TableRow(activity);
            row.setId(i);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            CheckBox checkBox = new CheckBox(activity);
            checkBox.setId(i);
            checkBox.setText(categories.get(i));
            checkBox.setBackground(activity.getDrawable(R.drawable.chekox_shape));
            checkBox.setPadding(30, 0, 30, 0);
            checkBox.setButtonDrawable(null);
            row.addView(checkBox);
            categoryContainer.addView(row);
        }
    }

    public String getStringTime(int time) {
        int hours = time / 60;
        int min = time % 60;
        if (hours == 0) {
            return min + " min";
        } else {
            return hours + " : " + min;
        }
    }

    public View.OnClickListener getOnClickListener(TextView textView, boolean isPlus) {
        return v -> {
            int sign = isPlus ? 1 : -1;
            if (isPlus) {
                int portions = mCurrentRecipe.getNumberOfServings();
                int currentPortions = Integer.valueOf(textView.getText().toString()) + sign;
                if (currentPortions == 0)
                    return;

                textView.setText(String.valueOf(currentPortions));
                productsAdapter.setKoeff(currentPortions / portions);
            }
        };
    }

    public void deleteRecipeFromDatabase(ControllableActivity activity, String recipeId) {
        AppDatabase db = AppDatabase.getDatabase(activity);
        db.getRecipeDao().deleteById(recipeId);
        db.getRelationCategoryRecipeDao().deleteAllWithId(recipeId);
        db.getRecipePrevDao().deleteAllById(recipeId);
    }


    public String getClosestToLine(String productLine, Context context) {
        int minDistance;
        List<String> closestIngredients = new ArrayList<>();
        for (int i = 0; i < productLine.split(" ").length; i++) {
            closestIngredients.add(getClosestIngredient(productLine.split(" ")[i], context));
        }

        String s1 = closestIngredients.get(0);
        minDistance = Utils.levenshteinDistance2(productLine, s1);
        int tempDistance;
        for (int i = 1; i < productLine.split(" ").length; i++) {
            String s2 = closestIngredients.get(i);
            tempDistance = Utils.levenshteinDistance2(productLine, s2);
            if (tempDistance < minDistance) {
                s1 = s2;
                minDistance = tempDistance;
            }
        }

        return s1;
    }

    public static String getClosestIngredient(String ingredient, Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        int tempDistance;
        int minDistance = -1;
        String closestIngredient = "";
        for (int i = 0; i < db.getIngredientDao().getAllIngredients().size() / 500; i++) {
            List<Ingredient> ingredientList = db.getIngredientDao().getIngredientRange(i * 500, 500);
            for (int j = 0; j < ingredientList.size(); j++) {
                String s = ingredientList.get(j).getEn();
                tempDistance = Utils.levenshteinDistance2(s, ingredient);
                if (minDistance == -1 || minDistance > tempDistance) {
                    minDistance = tempDistance;
                    closestIngredient = ingredientList.get(j).getEn();
                }

                if (tempDistance < 3) {
                    return ingredientList.get(j).getEn();
                }
            }
        }

        return closestIngredient;
    }

    public Rational getIngredientsAmount(String line) {
        line = line.toLowerCase();
        line = line.replaceAll("^(,| )+", "");
        Pattern p = Pattern.compile("((?:\\d+ )?\\d+(?:(?:,|.|/)\\d+)?)");
        Matcher m = p.matcher(line);
        if (!m.find()) {
            return new Rational(0, 1);
        }

        String amount = m.group(1);
        return Product.parseAmount(amount);
    }

    public void prepareProducts(Recipe recipe, Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        Product p;
        for (String string : recipe.getIngredientLines()) {
            p = new Product();
            List<Ingredient> list = db.getIngredientDao().getIngredientLike(string);
            p.setRationalAmount(getIngredientsAmount(string));
            p.setMeasure(MeasureUtils.matchMeasure(string));
            if (list.size() == 0) {
                String id = getClosestToLine(string, context);
                p.setIngredientId(id);
            } else {
                // вот тут еще посмотреть, потому что вместое chicken thighs
                // он просто выдает курицу, хотя мб там в списке оно есть
                // вместо sour cream выдает cream
                p.setIngredientId(list.get(0).getEn());
            }

            recipe.getProducts().add(p);
        }
    }
}
