package com.hotger.recipes.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.hotger.recipes.R;

import java.util.ArrayList;
import java.util.Arrays;

//TODO загружать данные о продуктах из бд (бд как-то первоначально надо инифиализировать)
/**
 * Adapter to show hint while typing product's name
 *
 * Адаптер для отображения всплывающих подсказок внутри редактора рецептов
 */
public class DataHintAdapter extends ArrayAdapter<String> {

    /**
     * Maximal number of products in hint
     */
    private final int MAX_COUNT = 5;

    private String[] string = {"Помидоры", "Капуста", "Картошка", "Кукуруза", "Кабачки",
            "Карамбола", "Куркума", "Корица", "Кориандр", "Огурцы", "Фарш говяжий", "Соль", "Сахар", "Молоко", "Вода",
    "сгущенка", "Butter", "Pork", "Carrot", "Broccoli", "Onion", "Egg", "Rice", "Frozen peas", "Soy sauce",
            "Garlic powder", "Ginger"};

    public DataHintAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource, R.id.product_name, new ArrayList<>());
        ArrayList<String> products = new ArrayList<>(Arrays.asList(string));
        addAll(products);
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
