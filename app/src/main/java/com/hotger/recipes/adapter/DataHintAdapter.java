package com.hotger.recipes.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.ArrayAdapter;

import com.hotger.recipes.R;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Adapter to show hint while typing product's name
 *
 * Адаптер для отображения всплывающих подсказок внутри редактора рецептов
 */
public class DataHintAdapter extends ArrayAdapter<String> {

    /**
     * Maximal number of products in hint
     */
    private final int MAX_COUNT = 3;

    public DataHintAdapter(@NonNull Context context, @LayoutRes int resource, AppDatabase db, String language) {
        super(context, resource, R.id.product_name, new ArrayList<>());
        clear();
        addAll(db.getIngredientDao().getEnglishNames());
        addAll(db.getIngredientDao().getRussianNames());
    }

    @Override
    public int getCount() {
        if (super.getCount() > MAX_COUNT) {
            return MAX_COUNT;
        } else {
            return super.getCount();
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
