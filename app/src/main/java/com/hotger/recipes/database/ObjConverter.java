package com.hotger.recipes.database;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;
import android.util.Rational;

import com.hotger.recipes.model.GsonModel.Image;
import com.hotger.recipes.model.GsonModel.Source;
import com.hotger.recipes.model.GsonModel.NutritionEstimates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Type converter for different types
 */
public class ObjConverter {
    @TypeConverter
    public String fromList(List<Image> urls) {
        List<String> array = new ArrayList<>();
        for(Image url : urls) {
            array.add(url.toString());
        }

        return TextUtils.join(", ", array);
    }

    @TypeConverter
    public List<Image> fromListURL(String data) {
        List<Image> url = new ArrayList<>();
        for (String s : Arrays.asList(data.split(","))) {
            url.add(Image.fromString(s));
        }

        return url;
    }

    @TypeConverter
    public String fromRational(Rational rational) {
        return rational.getNumerator() + "/" + rational.getDenominator();
    }

    @TypeConverter
    public Rational toRational(String data) {
        return new Rational(Integer.parseInt(data.split("/")[0]),
                Integer.parseInt(data.split("/")[1]));
    }

    @TypeConverter
    public String fromImage(Image image) {
        return image.getUrl();
    }

    @TypeConverter
    public Image toImage(String data) {
        return new Image(data);
    }

    @TypeConverter
    public String fromSource(Source source) {
        return source.getSourceRecipeUrl();
    }

    @TypeConverter
    public Source toSource(String data) {
        return new Source(data);
    }

    @TypeConverter
    public List<NutritionEstimates> toEstimates(String data) {
        if (data.length() == 0)
            return new ArrayList<>();

        List<NutritionEstimates> res = new ArrayList<>();
        for (String strEstimate : data.split("\\|")) {
            res.add(new NutritionEstimates(strEstimate.split(":")[0],
                    strEstimate.split(":")[1], strEstimate.split(":")[2]));
        }

        return res;
    }

    @TypeConverter
    public String fromEstimates(List<NutritionEstimates> estimates) {
        List<String> stringEstimates = new ArrayList<>();
        for(NutritionEstimates estimate : estimates) {
            stringEstimates.add(estimate.getAttribute() + ":" + estimate.getValue() + ":"
            + estimate.getDescription());
        }

        return TextUtils.join("|", stringEstimates);
    }
}
