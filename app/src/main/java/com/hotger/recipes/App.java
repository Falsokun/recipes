package com.hotger.recipes;

import android.app.Application;

import com.google.firebase.storage.FirebaseStorage;
import com.hotger.recipes.utils.TranslateAPI;
import com.hotger.recipes.utils.YummlyAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static YummlyAPI yummlyService;
    private static TranslateAPI translateService;
    private static FirebaseStorage storage ;

    @Override
    public void onCreate() {
        super.onCreate();
        storage = FirebaseStorage.getInstance();
    }

    public static YummlyAPI getApi() {
        if (yummlyService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(YummlyAPI.BASE_URL) //Базовая часть адреса
                    .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                    .build();
            yummlyService = retrofit.create(YummlyAPI.class); //Создаем объект, при помощи которого будем выполнять запросы
        }

        return yummlyService;
    }

    public static TranslateAPI getTranslateApi() {
        if (translateService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TranslateAPI.BASE_URL) //Базовая часть адреса
                    .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                    .build();
            translateService = retrofit.create(TranslateAPI.class); //Создаем объект, при помощи которого будем выполнять запросы
        }

        return translateService;
    }

    public static FirebaseStorage getStorage() {
        return storage;
    }
}
