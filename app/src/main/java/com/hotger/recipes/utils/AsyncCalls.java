package com.hotger.recipes.utils;

import com.hotger.recipes.App;
import com.hotger.recipes.database.RelationRecipeType;
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
                    return;
                }

                ArrayList<RecipePrev> prevs = response.body().getMatches();
                saveTypesToDB(prevs, searchValue, appDatabase);

                appDatabase.getRecipePrevDao()
                        .insertAll(prevs);
            }

            @Override
            public void onFailure(Call<ResponseRecipeAPI> call, Throwable t) {
            }
        };
    }

    private static void saveTypesToDB(ArrayList<RecipePrev> prevs, String type, AppDatabase database) {
        ArrayList<RelationRecipeType> relations = new ArrayList<>();
        for (RecipePrev prev : prevs) {
            relations.add(new RelationRecipeType(prev.getId(), type));
        }

        database.getRelationRecipeTypeDao().insertAll(relations);
    }
}
