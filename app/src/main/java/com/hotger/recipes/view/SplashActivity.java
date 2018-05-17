package com.hotger.recipes.view;

import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.daimajia.androidanimations.library.Techniques;
import com.hotger.recipes.R;
import com.hotger.recipes.database.FirebaseUtils;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.Utils;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class SplashActivity extends AwesomeSplash {

    private int initReferences = 0;

    @Override
    public void initSplash(ConfigSplash configSplash) {
        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.colorPrimary); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(500); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Customize Logo
        configSplash.setLogoSplash(R.mipmap.ic_launcher); //or any other drawable
        configSplash.setAnimLogoSplashDuration(100); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.Pulse); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        //Customize Path
        //configSplash.setPathSplash(Constants.DROID_LOGO); //set path String
        configSplash.setOriginalHeight(400); //in relation to your svg (path) resource
        configSplash.setOriginalWidth(400); //in relation to your svg (path) resource
        configSplash.setAnimPathStrokeDrawingDuration(100);
        configSplash.setPathSplashStrokeSize(3); //I advise value be <5
        configSplash.setPathSplashStrokeColor(R.color.colorAccent); //any color you want form colors.xml
        configSplash.setAnimPathFillingDuration(100);
        configSplash.setPathSplashFillColor(R.color.colorBackground); //path object filling color


        //Customize Title
        configSplash.setTitleSplash("");
//        configSplash.setTitleTextColor(Color.WHITE);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(100);
        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
//        configSplash.setTitleFont("fonts/myfont.ttf");
    }

    @Override
    public void animationsFinished() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "populus-database").allowMainThreadQueries().build();
        if (db.getIngredientDao().getAllIngredients().size() == 0) {
            LocalBroadcastManager
                    .getInstance(this)
                    .registerReceiver(getReceiver(), new IntentFilter(Utils.IntentVars.INIT_ON_START));
            FirebaseUtils.saveCategoryToDatabase(this);
            FirebaseUtils.saveIngredientsToDatabase(this);
        } else {
            openActivity();
        }
    }

    private void openActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        if (getIntent() != null) {
            Intent sent = getIntent();
            intent.putExtra(Utils.IntentVars.SHARE_TEXT, sent.getStringExtra(Intent.EXTRA_TEXT));
        }

        startActivity(intent);
        finish();
    }

    public BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initReferences++;
                if (initReferences == 2) {
                    LocalBroadcastManager.getInstance(SplashActivity.this).unregisterReceiver(this);
                    openActivity();
                }
            }
        };
    }
}
