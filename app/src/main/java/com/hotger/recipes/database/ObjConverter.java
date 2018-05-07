package com.hotger.recipes.database;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;
import android.util.Rational;

import com.hotger.recipes.model.GsonModel.Image;
import com.hotger.recipes.model.GsonModel.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public Rational fromString(String rationalStr) {
        return new Rational(Integer.parseInt(rationalStr.split("/")[0]),
                Integer.parseInt(rationalStr.split("/")[1]));
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
}
