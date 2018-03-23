package com.hotger.recipes.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.hotger.recipes.model.RecipePrev;

import java.util.List;

public class RecipePrevViewModel extends ViewModel {

    private LiveData<List<RecipePrev>> prevs;

    public LiveData<List<RecipePrev>> getAllPrevs(RecipePrevDao dao, String type) {
        if (prevs == null) {
            prevs = dao.getRecipesByType(type);
        }
        return prevs;
    }
}
