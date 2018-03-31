package com.hotger.recipes.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.hotger.recipes.database.dao.FavoritesDao;

import java.util.List;

public class FavoritesViewModel extends ViewModel {

    private LiveData<List<String>> prevs;

    public LiveData<List<String>> getAllFavorites(FavoritesDao dao) {
        if (prevs == null) {
            prevs = dao.getStringFavorites();
        }

        return prevs;
    }
}