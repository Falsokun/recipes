package com.hotger.recipes.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Rational;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.MeasureUtils;
import com.hotger.recipes.view.ControllableActivity;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Priority class
 */
@Entity(
        foreignKeys = @ForeignKey(entity = ApiRecipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE),
        primaryKeys = {"recipeId", "ingredientId"})
@IgnoreExtraProperties
public class Product implements Serializable {

    @NonNull
    private String recipeId;
    /**
     * Product name
     */
    @NonNull
    private String ingredientId;

    /**
     * Amount of product
     */
//    @TypeConverters({ObjConverter.class})
//    private Rational amount = new Rational(0, 1);
//
    private String amount = "1/1";

    /**
     * Measure of product
     */
    private String measure;

    @Ignore
    public Product() {
    }

    @Ignore
    public Product(String name, ControllableActivity activity) {
        findParamsInDB(activity, name);
    }

    @Ignore
    public Product(ControllableActivity activity, String ingredientId, Rational amount, String measure) {
        this.ingredientId = ingredientId;
        this.measure = measure;
        this.amount = amount.toString();
    }

    @Ignore
    public Product(String recipeId, String ingredientId, Rational amount, String measure) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.amount = amount.toString();
        this.measure = measure;
    }

    public Product(@NonNull String recipeId, @NonNull String ingredientId, String amount, String measure) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.amount = amount;
        this.measure = measure;
    }

    //TODO может тут лучше не парсинг, а посмотреть по базе чо есть (нооо это долго наверное)
    @Ignore
    public static Product getProductByLine(String productLine, ControllableActivity activity) {
        String line = productLine.replaceAll("\\(((\\w+\\s?)+)\\)(\\s|,)?", "");
        line = line.toLowerCase();
        line = line.replaceAll("^(,| )+", "");
        Pattern p = Pattern.compile("(\\d+)\\s(?:(cup|stick|pcs|ounce|kg|spoon|tablespoon)\\w?(?:\\s|,)+)?(([A-za-z,]+\\s?)+)");
        Matcher m = p.matcher(line);
        if (!m.matches()) {
            return new Product(line.substring(0, 1).toUpperCase() + line.substring(1), activity);
        }

        String amount = m.group(1);
        String measure = m.group(2);
        String productName = m.group(3).substring(0, 1).toUpperCase() + m.group(3).substring(1);
        return new Product(activity, productName, new Rational(Integer.parseInt(amount), 1), measure);
    }

    public void findParamsInDB(Context activity, String name) {
        List<Ingredient> ingredients = AppDatabase.getDatabase(activity).getIngredientDao().getIngredientByName(name);
        if (ingredients.size() == 0) {
            ingredientId = name;
            measure = MeasureUtils.DEFAULT_MEASURE;
            return;
        }

        Ingredient ingredient = AppDatabase.getDatabase(activity).getIngredientDao().getIngredientByName(name).get(0);
        ingredientId = ingredient.getEn();
    }

    @Ignore
    public static String getIngredientById(Context context, String ingredientId) {
        List<Ingredient> ingredients = AppDatabase
                .getDatabase(context)
                .getIngredientDao()
                .getIngredientByName(ingredientId);

        if (ingredients.size() != 0) {
            return ingredients.get(0).getTitle();
        }

        return ingredientId;
    }

    //region rational processing
    @Ignore
    public Rational getRationalAmount() {
        return Rational.parseRational(amount);
    }

    public void setRationalAmount(Rational amount) {
        this.amount = amount.toString();
    }

    /**
     * Get value with the multiplied coefficient
     *
     * @param rational - rational number
     * @param koeff    - koeffitient
     * @return string number of rational * koeff
     */
    public static CharSequence doubleToStringWithKoeff(Rational rational, double koeff) {
        Rational res;
        if ((int) koeff != koeff) {
            double newAmount = rational.doubleValue() * koeff;
            res = new Rational((int) (newAmount * 100), 100);
        } else {
            res = new Rational(rational.getNumerator() * (int) koeff, rational.getDenominator());
        }

        return getHtmlRational(res);
    }

    /**
     * Get rational in a convienient string way (e.g. 1/4, 0, 2 3/4)
     *
     * @param rational - rational number
     * @return rational string
     */
    @Ignore
    public static CharSequence getHtmlRational(Rational rational) {
        if (rational.getDenominator() == 1) {
            return String.valueOf(rational.getNumerator());
        }

        if (rational.getNumerator() == 0) {
            return "0";
        }

        if (rational.getNumerator() > rational.getDenominator()) {
            return Html.fromHtml(rational.getNumerator() / rational.getDenominator() +
                    " <small><sup>" + (rational.getNumerator() % rational.getDenominator()) + "</sup>/<sub>"
                    + rational.getDenominator() + "</sub></small>");
        }

        return Html.fromHtml("<small><sup>" + (rational.getNumerator() % rational.getDenominator()) + "</sup>/<sub>"
                + rational.getDenominator() + "</sub></small>");
    }

    @Ignore
    public static String getStringRational(Rational rational) {
        if (rational.getDenominator() == 1) {
            return String.valueOf(rational.getNumerator());
        }

        if (rational.getNumerator() == 0) {
            return "0";
        }

        if (rational.getNumerator() > rational.getDenominator()) {
            return rational.getNumerator() / rational.getDenominator() +
                    " " + (rational.getNumerator() % rational.getDenominator()) + "/"
                    + rational.getDenominator();
        }

        return rational.getNumerator() % rational.getDenominator() + "/"
                + rational.getDenominator();
    }

    /**
     * Get from String
     *
     * @param amount - string rational
     * @return rational number
     */
    public static Rational parseAmount(String amount) {
        if (amount.contains("/")) {
            if (!amount.contains(" ")) {
                return Rational.parseRational(amount);
            } else {
                int denum = Integer.parseInt(amount.split("/")[1]);
                int num = Integer.parseInt(amount.split("/")[0].split(" ")[0]) * denum
                        + Integer.parseInt(amount.split("/")[0].split(" ")[1]);
                return new Rational(num, denum);
            }
        }

        if (amount.contains(",") || amount.contains(".")) {
            String temp = amount.replace(",", ".");
            int res = (int) (Double.parseDouble(temp) * 100);
            return new Rational(res, 100);
        }


        if (amount.contains(" "))
            amount = amount.split(" ")[0];

        if (amount.contains("-")) {
            amount = amount.split("-")[1];
        }
        return new Rational(Integer.parseInt(amount), 1);
    }
    //endregion

    //region Getters and Setters
    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getMeasure() {
        return measure;
    }

    public int getDrawableByMeasure() {
        return 0;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }
    //endregion

    public String asString(Context context) {
        return getStringRational(Rational.parseRational(amount)) + " " + measure + " " + getIngredientById(context, ingredientId);
    }


}
