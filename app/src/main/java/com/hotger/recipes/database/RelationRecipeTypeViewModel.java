package com.hotger.recipes.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.hotger.recipes.database.dao.RecipePrevDao;
import com.hotger.recipes.database.dao.RelationRecipeTypeDao;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.utils.Utils;

import java.util.List;

public class RelationRecipeTypeViewModel extends ViewModel {

    private LiveData<List<String>> myRecipes;
    private LiveData<List<String>> favs;

    public LiveData<List<String>> getAllPrevs(RelationRecipeTypeDao dao) {
        if (myRecipes == null) {
            myRecipes = dao.getLiveRecipesByType(Utils.TYPE.TYPE_MY_RECIPES);
        }

        return myRecipes;
    }

    public LiveData<List<String>> getAllFavs(RelationRecipeTypeDao dao) {
        if (favs == null) {
            favs = dao.getLiveRecipesByType(Utils.TYPE.TYPE_MY_FAVS);
        }

        return favs;
    }
}
