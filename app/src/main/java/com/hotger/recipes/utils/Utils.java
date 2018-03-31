package com.hotger.recipes.utils;

import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.hotger.recipes.R;

import java.lang.reflect.Field;
import java.util.Locale;

public class Utils {

    public static final SparseIntArray bottomNavigationTabs = new SparseIntArray();
    public static final String EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID";
    public static final String ENGLISH = "en";
    public static final String FIREBASE_IMG_STORAGE = "recipe_image";
    public static final String EXTRA_TYPE = "type";

    static {
        bottomNavigationTabs.put(R.id.menu_home, 0);
        bottomNavigationTabs.put(R.id.menu_categories, 1);
        bottomNavigationTabs.put(R.id.menu_fridge, 2);
        bottomNavigationTabs.put(R.id.menu_profile, 3);
        bottomNavigationTabs.put(R.id.menu_search, 4);
    }

    //TODO: formatter
    public static String numberToString(double number) {
        if (number % 1 == 0) {
            return String.valueOf((int)number);
        }

        return String.valueOf(number);
    }

    public static boolean isRussian() {
        return Locale.getDefault().toString().contains("ru");
    }

    /**
     * Measure variables
     */
    public class Measure {
        public static final int NONE = 0;
        public static final int LITERS = 1;
        public static final int KG = 2;
        public static final int GRAMM = 3;
        public static final int CUPS = 4;
        public static final int TEASPOON = 5;
        public static final int TABLESPOON = 6;
        public static final int PIECE = 7;
        public static final int OUNCE = 8;
    }

    public class SharedPref {
        public static final String TRANSLATIONS_REF = "translations_ref";
        public static final String PUBLISH_REF = "publish_ref";
    }

    //TODO тут наверное надо все это убрать и сделать красиво
    public static final String STATE = "State";
    public static final int NUMBER_PICKER = 0;
    public static final int TIME_PICKER = 1;
    public static final String RECIPE_ID = "RECIPE_ID";
    public static final String RECIPE_OBJ = "RECIPE_OBJ";
    public static final String RECIPE_CATEGORY = "RECIPE_CATEGORY";
    public static final String NEED_INIT = "NEED_INIT";

    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public static String getKeyboardLanguage(Context context) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

        InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();

        String locale = ims.getLocale();

        return locale;
    }

    private String toUpperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static class TYPE {
        public static String TYPE_MY_RECIPES = "MY_RECIPES";
        public static String TYPE_MY_FAVS = "MY_FAVS";
    }
}
