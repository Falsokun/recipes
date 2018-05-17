package com.hotger.recipes.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hotger.recipes.R;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.view.ControllableActivity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID";
    public static final SparseIntArray bottomNavigationTabs = new SparseIntArray();

    static {
        bottomNavigationTabs.put(R.id.menu_home, 0);
        bottomNavigationTabs.put(R.id.menu_categories, 1);
        bottomNavigationTabs.put(R.id.menu_fridge, 2);
        bottomNavigationTabs.put(R.id.menu_profile, 3);
        bottomNavigationTabs.put(R.id.menu_search, 4);
    }

    public static void hideKeyboard(Activity activity) {
        View v = activity.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static boolean matchesUrl(String str) {
        ///^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$/
        Pattern urlPattern = Pattern.compile("^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$");
        Matcher m = urlPattern.matcher(str);
        return m.find();
    }

    public static RecipePrev getRandomPrev(ControllableActivity activity) {
        List<RecipePrev> all = AppDatabase.getDatabase(activity).getRecipePrevDao().getAll();
        Random random = new Random();
        int num = random.nextInt(all.size());
        return all.get(num);
    }

    /**
     * Shared preferences variables
     */
    public class SharedPref {
        public static final String TRANSLATIONS_REF = "translations_ref";
        public static final String PUBLISH_REF = "publish_ref";
    }

    /**
     * Params to pass through intents
     */
    public class IntentVars {
        public static final String EXTRA_TYPE = "type";
        public static final String RECIPE_ID = "RECIPE_ID";
        public static final String RECIPE_OBJ = "RECIPE_OBJ";
        public static final String RECIPE_CATEGORY = "RECIPE_CATEGORY";
        public static final String RECIPE_TYPE = "RECIPE_TYPE";
        public static final String NEED_INIT = "NEED_INIT";
        public static final String SHOULD_OPEN_RECIPE = "SHOULD_OPEN_RECIPE";
        public static final String SHARE_TEXT = "SHARE_TEXT";
        public static final String INIT_ON_START = "INIT_ON_START";
        public static final String INIT_GESTURES = "INIT_GESTURES";
        public static final String SHOULD_WAIT = "SHOULD_WAIT";
    }

    /**
     * Recipe types
     */
    public static class SP_RECIPES_ID {
        public static String TYPE_MY_RECIPES = "recipes_id";
        public static String TYPE_MY_FAVS = "favs_id";
        public static String TYPE_BOOKMARK = "bookmarks_id";
        public static String TYPE_FRIDGE_ID = "fridge_products_id";
        public static String TYPE_SHOPPING_LIST = "shopping_list_id";
        public static String TYPE_SL_CHECKED = "shopping_list_checked_id";
        public static String TYPE_SL_UNCHECKED = "shopping_list_un_checked_id";
    }

    //TODO: formatter
    public static String numberToString(double number) {
        if (number % 1 == 0) {
            return String.valueOf((int) number);
        }

        return String.valueOf(number);
    }

    /**
     * If current locale is russian
     *
     * @return <code>true</code> if is russian language
     */
    public static boolean isRussian() {
        return Locale.getDefault().toString().contains("ru");
    }

    /**
     * Disable bottom navigation shifting
     *
     * @param view bottom navigation
     */
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

    public static int levenshteinDistance2(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[] D1;
        int[] D2 = new int[n + 1];

        for (int i = 0; i <= n; i++)
            D2[i] = i;

        for (int i = 1; i <= m; i++) {
            D1 = D2;
            D2 = new int[n + 1];
            for (int j = 0; j <= n; j++) {
                if (j == 0) D2[j] = i;
                else {
                    int cost = (s1.charAt(i - 1) != s2.charAt(j - 1)) ? 1 : 0;
                    if (D2[j - 1] < D1[j] && D2[j - 1] < D1[j - 1] + cost)
                        D2[j] = D2[j - 1] + 1;
                    else if (D1[j] < D1[j - 1] + cost)
                        D2[j] = D1[j] + 1;
                    else
                        D2[j] = D1[j - 1] + cost;
                }
            }
        }
        return D2[n];
    }
}
