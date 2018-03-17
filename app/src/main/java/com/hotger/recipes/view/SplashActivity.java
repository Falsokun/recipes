package com.hotger.recipes.view;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hotger.recipes.firebase.RealtimeDB;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.AsyncCalls;
import com.hotger.recipes.utils.model.Category;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "populus-database").allowMainThreadQueries().build();
        RealtimeDB.saveIngredientsToDatabase(db);
        RealtimeDB.saveIngredientsToDatabase(db, RealtimeDB.CATEGORY);
        saveToDatabase(db);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveToDatabase(AppDatabase db) {
        for (Category category : db.getCategoryDao().getAllCategories()) {
            AsyncCalls.saveCategoryToDB(this, db,
                    category.getSearchValue());
        }
    }
}
