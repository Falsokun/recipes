package com.hotger.recipes.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.ActivitySettingsBinding;
import com.hotger.recipes.utils.Utils;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding mBinding;
    SharedPreferences pref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        setSupportActionBar(mBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(R.string.action_settings);
        }

        pref  = getPreferences(Context.MODE_PRIVATE);
        mBinding.switch1
                .setChecked(pref.getBoolean(Utils.SharedPref.PUBLISH_REF, true));
        mBinding.switch2
                .setChecked(pref.getBoolean(Utils.SharedPref.TRANSLATIONS_REF, true));
        mBinding.switch1
                .setOnCheckedChangeListener(getOnCheckedChangeListener(Utils.SharedPref.PUBLISH_REF));
        mBinding.switch2
                .setOnCheckedChangeListener(getOnCheckedChangeListener(Utils.SharedPref.TRANSLATIONS_REF));
    }

    public CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener(String prefString) {
        return (buttonView, isChecked) -> pref.edit().putBoolean(prefString, isChecked);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
