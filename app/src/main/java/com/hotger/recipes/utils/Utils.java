package com.hotger.recipes.utils;

import android.app.Activity;
import android.util.SparseIntArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hotger.recipes.R;

public class Utils {

    public static final SparseIntArray bottomNavigationTabs = new SparseIntArray();
    public static final String EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID";;

    static {
        bottomNavigationTabs.put(R.id.menu_home, 0);
        bottomNavigationTabs.put(R.id.menu_my_recipes, 1);
        bottomNavigationTabs.put(R.id.menu_other, 2);
    }

    /**
     * Hides keyboard from the screen
     *
     * @param view - value for the context
     */
    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)view.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String numberToString(double number) {
        if (number % 1 == 0) {
            return String.valueOf((int)number);
        }

        return String.valueOf(number);
    }

    /**
     * Measure variables
     */
    public class Measure {
        public static final int LITERS = 0;
        public static final int KG = 1;
        public static final int GRAMM = 2;
        public static final int CUPS = 3;
        public static final int TEASPOON = 4;
        public static final int TABLESPOON = 5;
        public static final int PIECE = 6;

    }

    //TODO тут наверное надо все это убрать и сделать красиво
    public static final String STATE = "State";
    public static final int NUMBER_PICKER = 0;
    public static final int TIME_PICKER = 1;
    public static final String RECIPE_ID = "RECIPE_ID";

}
