package com.hotger.recipes.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hotger.recipes.App;
import com.hotger.recipes.database.relations.RelationRecipeType;
import com.hotger.recipes.model.RecipePrev;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsyncCalls {

    /**
     * Save categories to database
     * @param context
     * @param searchValue
     * @param sendMessage
     */
    public static void saveCategoryToDB(Context context, String searchValue, boolean sendMessage) {
        Call<ResponseAPI> call;
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

        call.enqueue(getCallback(context, searchValue, sendMessage));
    }

    public static Callback<ResponseAPI> getCallback(Context context, String searchValue, boolean shouldSendMessage) {
        return new Callback<ResponseAPI>() {
            @Override
            public void onResponse(Call<ResponseAPI> call, Response<ResponseAPI> response) {
                if (response.body() == null) {
                    return;
                }

                AppDatabase appDatabase = AppDatabase.getDatabase(context);
                ArrayList<RecipePrev> prevs = response.body().getMatches();
                saveTypesToDB(prevs, searchValue, appDatabase);

                appDatabase.getRecipePrevDao()
                        .insertAll(prevs);

                if (shouldSendMessage) {
                    Intent intent = new Intent(Utils.IntentVars.NEED_INIT);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseAPI> call, Throwable t) {
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
