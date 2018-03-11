package com.hotger.recipes;

import android.app.Application;

import com.hotger.recipes.utils.YummlyAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static YummlyAPI yummlyService;

    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl(YummlyAPI.BASE_URL) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        yummlyService = retrofit.create(YummlyAPI.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public static YummlyAPI getApi() {
        return yummlyService;
    }
}
