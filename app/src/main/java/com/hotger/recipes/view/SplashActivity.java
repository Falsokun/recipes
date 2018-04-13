package com.hotger.recipes.view;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hotger.recipes.R;
import com.hotger.recipes.firebase.FirebaseUtils;
import com.hotger.recipes.utils.AppDatabase;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "populus-database").allowMainThreadQueries().build();
        if (db.getRecipePrevDao().getAll().size() == 0) {
            FirebaseUtils.saveIngredientsToDatabase(db);
            FirebaseUtils.saveCategoryToDatabase(this, db, FirebaseUtils.CATEGORY);
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
