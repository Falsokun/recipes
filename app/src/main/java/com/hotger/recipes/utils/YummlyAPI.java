package com.hotger.recipes.utils;

import com.hotger.recipes.model.RecipeNF;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface YummlyAPI {

    String SEARCH = YummlyAPI.BASE_URL + YummlyAPI.RECIPES_PATH + "&" + YummlyAPI.BASE + "&"; //YummlyAPI.MAX_RESULT +
    String APP_ID = "42e79ceb"; //"42e79ceb";
    String APP_KEY = "12d332fad2bb304db75ae40ec0ab6a32";//"12d332fad2bb304db75ae40ec0ab6a32";
    String BASE_URL = "http://api.yummly.com/";
    String RECIPES_PATH = "v1/api/recipes?";
    String BASE = "_app_id=" + APP_ID + "&_app_key=" + APP_KEY;
    int MAX_RESULT = 300;
    String ALLOWED_CUISINE_PARAM = "&allowedCuisine[]=";
    String ALLOWED_DIET_PARAM = "&allowedDiet[]=";
    String ALLOWED_COURSE = "&allowedCourse[]=";

    String REC_DIRECTIONS = "RECIPE_DIRECTIONS";

    @GET("/v1/api/recipe/{id}?" + BASE)
    Call<RecipeNF> getRecipeByID(@Path("id") String recipeID);

    @GET("/v1/api/recipes?" + BASE)
    Call<ResponseRecipeAPI> getCategoryList(@Query("allowedCuisine[]") String cuisine, @Query("maxResult") int maxResult);

    @GET("/v1/api/recipes?" + BASE)
    Call<ResponseRecipeAPI> getHolidayList(@Query("allowedHoliday[]") String holiday, @Query("maxResult") int maxResult);

    @GET("/v1/api/recipes?" + BASE)
    Call<ResponseRecipeAPI> getCourseList(@Query("allowedCourse[]") String course, @Query("maxResult") int maxResult);

    @GET("/v1/api/recipes?" + BASE)
    Call<ResponseRecipeAPI> getDietList(@Query("allowedDiet[]") String diet, @Query("maxResult") int maxResult);

    @GET("/v1/api/" + BASE)
    Call<ResponseRecipeAPI> searchByIngredients(@Query("") String searchIngredients, @Query("maxResult") int maxResult);

    @GET
    Call<ResponseRecipeAPI> search(@Url String url);

    class Description {
        public final static String HOLIDAY = "holiday";
        public final static String CUISINE = "cuisine";
        public final static String COURSE = "course";
        public final static String DIET = "diet";
    }
}
