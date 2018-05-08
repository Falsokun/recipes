package com.hotger.recipes.utils;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TranslateAPI {

    String API_KEY = "key=trnsl.1.1.20180331T110910Z.b27cad1c9db5f675.99651a7a2790a10de243d668869e0b9f357e8e2d";
    String BASE_URL = "https://translate.yandex.net/";
    //&text="sample"&lang=en-ru&format=plain
    String EN_RU = "en-ru";

    @GET("/api/v1.5/tr.json/translate?" + API_KEY)
    Call<TranslateResponse> translate(@Query("text") String text, @Query("lang") String lang);
}
