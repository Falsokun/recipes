package com.hotger.recipes.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.hotger.recipes.App;
import com.hotger.recipes.utils.model.RecipePrev;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsyncCalls {

    //category = allowedCuisine[]
    public static void saveCategoryToDB(Activity activity, AppDatabase appDatabase, String searchValue) {
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

        call.enqueue(getCallback(activity, appDatabase, searchValue));
    }

    public static Callback<ResponseRecipeAPI> getCallback(Activity activity, AppDatabase appDatabase, String searchValue) {
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
                Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show();
            }
        };
    }

//    public static void getFromMetadata(String metadata) {
//        String data = "ingredient";
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                String str = "";
//                try {
//                    URL url = new URL(YummlyAPI.BASE_URL + "v1/api/metadata/" + data + "?" + YummlyAPI.BASE);
//                    BufferedReader reader = null;
//                    reader = new BufferedReader(new InputStreamReader(url.openStream()));
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        str += line;
//                    }
//                    reader.close();
//                    str = str.replace("set_metadata('" + data + "', ", "");
//                    str = str.substring(0, str.length() - 2);// + "}";
//                    JSONArray jsonArray = new JSONArray(str);
//                    JSONArray newArray = new JSONArray();
//                    for(int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                        JSONObject newObj = new JSONObject();
//                        newObj.put("en", jsonObject.getString("searchValue"));
//                        newObj.put("ru", "");
//                        newObj.put("measure", "gr");
//                        newArray.put(newObj);
//                    }
//
//                    Log.d("TAG", "text");
////                    Gson gson = new Gson();
////                    ArrayList<Product> products = gson.fromJson(str, new TypeToken<ArrayList<Product>>() {
////                    }.getType());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        Thread t = new Thread(runnable);
//        t.start();
//    }
}