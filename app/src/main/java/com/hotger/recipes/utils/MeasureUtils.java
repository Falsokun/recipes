package com.hotger.recipes.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.hotger.recipes.R;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeasureUtils {
    public static final String DEFAULT_MEASURE = "pcs";
    private static List<String> TSP = Arrays.asList("tsp", "ч\\. л\\.", "чайн.?.? ложк", "teaspoon", "t");
    private static List<String> TBSP = Arrays.asList("tbsp", "ст\\. л\\.", "столов.?.? ложк", "tablespoon", "tbl", "dessertspoon", "T", "tbs");
    private static List<String> DRY = Arrays.asList("kg", "кг", "килограмм", "грамм", "г", "kilogram", "gram", "g",
            "gr", "bu", "bsh", "bushel", "pound", "lb", "ounce", "oz");
    private static List<String> L = Arrays.asList("l", "литр", "литр", "л", "liter", "litre");
    private static List<String> ML = Arrays.asList("ml", "мл", "миллилитр", "milliliter", "millilitre");
    private static List<String> LIQUID = Arrays.asList("cup.?", "стакан", "peck", "pk", "glass", "dash",
            "pinch", "pn", "gallon", "quart", "qt", "pint", "pt", "c",
            "fl oz", "gill", "gallon", "gal", "quart", "qt", "pint", "pt", "стакан");
    private static List<String> LITTLE = Arrays.asList("smidgen", "drop", "clove",
            "зубчик", "щепотк", "кап[ельяию]{2,3}");
    private static List<String> PCS = Arrays.asList("pcs", "pieces?", "штук", "шт");

    private static List<List<String>> measures = Arrays.asList(TBSP, TSP, ML, L, DRY, LIQUID, LITTLE, PCS);

    public static String matchMeasure(String str) {
        for (List<String> tempUnit : measures) {
            Pattern p = Pattern.compile("(" + TextUtils.join("\\|", tempUnit) + ").*?[. ,]");
            Matcher m;
            if (tempUnit != TBSP) {
                m = p.matcher(str.toLowerCase());
            } else {
                m = p.matcher(str);
            }
            if (m.find()) {
                if (tempUnit == LIQUID || tempUnit == LITTLE) { //тут сравнения по ссылке достаточно
                    return m.group(1);
                }

                return tempUnit.get(0);
            }
        }

        return DEFAULT_MEASURE;
    }

    /**
     * Returns drawable depending on {@param measure} of the product
     *
     * @param measure - quanitity of the product
     * @param context - activity
     * @return drawable received from the resource
     */
    public static Drawable getDrawableByMeasure(Context context, String measure) {
        Drawable drawable;
        if (L.contains(measure)
                || ML.contains(measure)
                || LIQUID.contains(measure)) {
            drawable = context.getDrawable(R.drawable.ic_water);
        } else if (DRY.contains(measure)) {
            drawable = context.getDrawable(R.drawable.ic_gram);
        } else if (TSP.contains(measure) || TBSP.contains(measure)) {
            drawable = context.getDrawable(R.drawable.ic_spoon);
        } else {
            drawable = context.getResources().getDrawable(R.drawable.ic_pcs_white);
        }

        return drawable;
    }

    public static String getFromArray(int position) {
        switch (position) {
            case 1:
            case 4:
                return L.get(0);
            case 2:
            case 3:
                return DRY.get(0);
            case 5:
            case 6:
                return TBSP.get(0);
            default:
                return LITTLE.get(0);
        }
    }
}
