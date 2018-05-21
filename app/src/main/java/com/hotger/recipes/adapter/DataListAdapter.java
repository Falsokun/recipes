package com.hotger.recipes.adapter;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.hotger.recipes.R;
import com.hotger.recipes.model.Ingredient;
import com.hotger.recipes.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Adapter to show hint while typing product's name
 * <p>
 * Адаптер для отображения всплывающих подсказок внутри редактора рецептов
 */
public class DataListAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> allData = new ArrayList<>();

    private List<String> data = new ArrayList<>();

    private String prevSeq = "";

    private int FILTERING_LENGTH = 3;

    private ObservableBoolean isEmpty = new ObservableBoolean(false);

    public DataListAdapter(@NonNull Context context, @LayoutRes int resource, List<String> data) {
        super(context, resource, R.id.name, data);
        this.data = data;
        this.allData = new ArrayList<>(data);
        isEmpty.set(true);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                // not filtering by 2 symbols
                if (constraint.length() < FILTERING_LENGTH) {
                    if (data.size() != allData.size()) {
                        data.clear();
                        data.addAll(allData);
                    }

                    result.values = data;
                    result.count = data.size();
                    return result;
                }

                if (prevSeq.length() > constraint.length()
                        || data.size() == 0) {
                    data.clear();
                    data.addAll(allData);
                    if (constraint.length() < FILTERING_LENGTH) {
                        result.values = data;
                        result.count = data.size();
                        return result;
                    }
                }
                if (constraint.charAt(constraint.length() - 1) == ' ') {
                    result.values = new ArrayList<>(data);
                    result.count = data.size();
                } else {
                    ArrayList<String> filteredList = new ArrayList<>();
                    for (String ingredient : data) {
                        String[] words = constraint.toString().split(" ");
                        boolean shouldInclude = true;
                        for (String word : words) {
                            if (!ingredient.toLowerCase().contains(word)) {
                                shouldInclude = false;
                                break;
                            }
                        }

                        if (shouldInclude) {
                            filteredList.add(ingredient);
                        }
                    }

                    mostRelevant(filteredList, constraint);
                    result.values = filteredList;
                    result.count = filteredList.size();
                }

                prevSeq = constraint.toString();
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data.clear();
                if (results.values != null) {
                    data.addAll((ArrayList<String>) results.values);
                }

                notifyDataSetChanged();
                isEmpty.set(data.size() == 0 || data.size() == allData.size());
                isEmpty.notifyChange();
            }
        };
    }

    private void mostRelevant(ArrayList<String> filteredList, CharSequence constraint) {
        Collections.sort(filteredList, (o1, o2) -> {
            String str = constraint.toString();
            return Utils.levenshteinDistance2(o1, str) -
                    Utils.levenshteinDistance2(o2, str);
        });
    }

    public ObservableBoolean isEmptyData() {
        return isEmpty;
    }

    public void addIngredient(Ingredient ingredient) {
        data.add(ingredient.getEn());
        data.add(ingredient.getRu());
        allData.add(ingredient.getEn());
        allData.add(ingredient.getRu());
    }
}
