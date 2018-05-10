package com.hotger.recipes.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.hotger.recipes.database.dao.RelationsDao;

import java.util.List;

//TODO: пока удалять не буду, но мне кажется, это имеет отношения к категориям
public class FavoritesViewModel extends ViewModel {

    private LiveData<List<String>> prevs;

    public LiveData<List<String>> getAllFavorites(RelationsDao dao) {
        if (prevs == null) {
            prevs = dao.getStringFavorites();
        }

        return prevs;
    }
}