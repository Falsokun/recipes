package com.hotger.recipes.view;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hotger.recipes.firebase.FirebaseUtils;
import com.hotger.recipes.utils.AppDatabase;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "populus-database").allowMainThreadQueries().build();
        if (db.getRecipeDao().getAll().size() == 0) {
            FirebaseUtils.saveIngredientsToDatabase(db);
            FirebaseUtils.saveCategoryToDatabase(db, FirebaseUtils.CATEGORY);
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
