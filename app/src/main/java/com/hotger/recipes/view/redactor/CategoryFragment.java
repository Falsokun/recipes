package com.hotger.recipes.view.redactor;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentRedactorCategoryBinding;
import com.hotger.recipes.utils.RealmString;
import com.hotger.recipes.viewmodel.RedactorViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

//TODO База данных
public class CategoryFragment extends Fragment {

    /**
     * Data binding variable
     */
    private FragmentRedactorCategoryBinding mBinding;

    /**
     * View model variable
     */
    private RedactorViewModel model;

    /**
     * Map of Category:Tags (K:V)
     */
    private HashMap<String, ArrayList<String>> allTags;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_redactor_category, container, false);
        initAllTags();
        addCategories();
        return mBinding.getRoot();
    }

    //TODO База данных
    /**
     * Get Categories and tags within it
     */
    private void initAllTags() {
        allTags = new HashMap<>();
        String[] categories = {"Baked", "Breakfast", "Cooking Level", "Cuisine", "Desert", "Healthy"};

        for (String category : categories) {
            allTags.put(category, new ArrayList<>());
        }

        allTags.get(categories[0]).addAll(Arrays.asList("Cookies", "Cakes", "Bread"));
        allTags.get(categories[1]).addAll(Arrays.asList("Omelets", "Porridge", "Frittata", "Waffles"));
        allTags.get(categories[2]).addAll(Arrays.asList("Hard", "Easy", "Middle", "Super easy"));
        allTags.get(categories[3]).addAll(Arrays.asList("European", "Asian", "Chinese", "Mexican", "Japanese", "Indian"));
        allTags.get(categories[4]).addAll(Arrays.asList("Pudding", "Ice cream", "Candy", "Jelly"));
        allTags.get(categories[5]).addAll(Arrays.asList("low fat", "diabetic", "high fiber"));
    }

    /**
     * Add all categories and tags to the container
     */
    private void addCategories() {
        Set<String> categories = allTags.keySet();
        for (String category : categories) {
            addCategoryRow(category, allTags.get(category), false);
        }
    }

    /**
     * Adding {@param category} row with corresponding {@param tags} to the container
     *
     * @param category - category
     * @param tags     - corresponding tags
     * @param isLast   - if the row is last
     */
    private void addCategoryRow(String category, ArrayList<String> tags, boolean isLast) {
        TextView textView = new TextView(getContext());
        textView.setText(category);
        textView.setTextColor(getResources().getColor(R.color.colorText));
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mBinding.categoryContainer.addView(textView);

        for (int i = 0; i < tags.size(); i++) {
            TableRow row = new TableRow(getContext());
            row.setId(i);
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setId(i);
            checkBox.setText(tags.get(i));
            if (model.isEdited() && model.getCurrentRecipe().hasCategory(tags.get(i))) {
                checkBox.setSelected(true);
            }

            checkBox.setBackground(getResources().getDrawable(R.drawable.chekox_shape));
            checkBox.setPadding(30, 0, 30, 0);
            checkBox.setButtonDrawable(null);
            checkBox.setTag(tags.get(i));
            checkBox.setOnCheckedChangeListener(model.getCheckedListener());
            row.addView(checkBox);
            mBinding.categoryContainer.addView(row);
        }

        if (!isLast) {
            View border = new View(getContext());
            border.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    2));
            border.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            mBinding.categoryContainer.addView(border);
        }
    }

    public void setModel(RedactorViewModel model) {
        this.model = model;
    }
}
