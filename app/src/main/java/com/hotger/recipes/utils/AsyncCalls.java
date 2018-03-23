package com.hotger.recipes.utils;

import android.app.Activity;
import android.widget.Toast;

import com.hotger.recipes.App;
import com.hotger.recipes.model.RecipePrev;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsyncCalls {

    //category = allowedCuisine[]
    public static void saveCategoryToDB(AppDatabase appDatabase, String searchValue) {
        Call<ResponseRecipeAPI> call;
        YummlyAPI api = App.getApi();
        String category = searchValue.split("\\^")[0];
        switch (category) {
            case YummlyAPI.Description.CUISINE:
                call = api.getCategoryList(searchValue, YummlyAPI.MAX_RESULT);
                break;
            case YummlyAPI.Description.COURSE:
                call = api.getCourseList(searchValue, YummlyAPI.MAX_RESULT);
                break;
            case YummlyAPI.Description.HOLIDAY:
                call = api.getHolidayList(searchValue, YummlyAPI.MAX_RESULT);
                break;
            default:
                call = api.getDietList(searchValue, YummlyAPI.MAX_RESULT);
        }

        call.enqueue(getCallback(appDatabase, searchValue));
    }

    public static Callback<ResponseRecipeAPI> getCallback(AppDatabase appDatabase, String searchValue) {
        return new Callback<ResponseRecipeAPI>() {
            @Override
            public void onResponse(Call<ResponseRecipeAPI> call, Response<ResponseRecipeAPI> response) {
                if (response.body() == null) {
                    //видимо слишком перегружаю
                    return;
                }

                ArrayList<RecipePrev> prevs = response.body().getMatches();
                for (RecipePrev prev : prevs) {
                    prev.setType(searchValue);
                }

                appDatabase.getRecipePrevDao()
                        .insertAll(prevs);
            }

            @Override
            public void onFailure(Call<ResponseRecipeAPI> call, Throwable t) {
            }
        };
    }
}
