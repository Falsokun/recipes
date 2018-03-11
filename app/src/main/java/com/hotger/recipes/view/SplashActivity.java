package com.hotger.recipes.view;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.AsyncCalls;
import com.hotger.recipes.utils.StaticFunctions;
import com.hotger.recipes.utils.model.Category;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveToDatabase();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveToDatabase() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "populus-database").allowMainThreadQueries().build();
        db.getCategoryDao().insertAll(StaticFunctions.getCuisineCategories());
        db.getCategoryDao().insertAll(StaticFunctions.getHolidayCategories());
        db.getCategoryDao().insertAll(StaticFunctions.getCourse());
        db.getCategoryDao().insertAll(StaticFunctions.getDiets());
        for (Category category : db.getCategoryDao().getAllCategories()) {
            AsyncCalls.saveCategoryToDB(this, db,
                    category.getSearchValue());
        }
    }

    private String toUpperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
