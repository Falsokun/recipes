package com.hotger.recipes.view.redactor;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.ViewPagerAdapter;
import com.hotger.recipes.databinding.ActivityRedactorBinding;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.ControllableActivity;
import com.hotger.recipes.viewmodel.RedactorViewModel;

public class RedactorActivity extends ControllableActivity {

    /**
     * Binding variable
     */
    private ActivityRedactorBinding mBinding;

    /**
     * Redactor model (model for every child fragment)
     */
    private RedactorViewModel mRedactorModel;

    /**
     * Adapter for viewpager
     */
    private ViewPagerAdapter mRedactorAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getStringExtra(Utils.IntentVars.RECIPE_ID) != null) {
            mRedactorModel = new RedactorViewModel(this,
                    getIntent().getStringExtra(Utils.IntentVars.RECIPE_ID));
        } else {
            mRedactorModel = new RedactorViewModel(this);
        }

        mRedactorModel.setEdited(true);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_redactor);
        mBinding.setModel(mRedactorModel);
        initAdapter();
        mBinding.recipeVp.setAdapter(mRedactorAdapter);
        mBinding.recipeVp.setOffscreenPageLimit(mRedactorAdapter.getCount());
        mBinding.redactorProgress.setProgress(getProgress(0));
        initListeners();
        initToolbar();
        mBinding.btnSave.setOnClickListener(view -> {
            if (mBinding.btnSave.getText()
                    .equals(getResources().getString(R.string.next))) {
                mBinding.recipeVp.setCurrentItem(mBinding.recipeVp.getCurrentItem() + 1);
            } else {
                boolean result = mRedactorModel.onSave(mBinding.recipeVp);
                if (result) {
                    onBackPressed();
                    Intent intent = new Intent(Utils.IntentVars.RECIPE_ID);
                    intent.putExtra(Utils.IntentVars.RECIPE_ID, mRedactorModel.getCurrentRecipe());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void initToolbar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.redactor));
    }
    /**
     * Adding to viewpager all fragments of the recipe
     */
    private void initAdapter() {
        if (mRedactorAdapter == null) {
            mRedactorAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            ProductsRedactorFragment fragment = new ProductsRedactorFragment();
            fragment.setInputModel(mRedactorModel.getInputProductsViewModel());
            mRedactorAdapter.addFragment(fragment);

            CategoryRedactorFragment fragment1 = new CategoryRedactorFragment();
            fragment1.setModel(mRedactorModel);
            mRedactorAdapter.addFragment(fragment1);

            TextRedactorFragment fragment2 = new TextRedactorFragment();
            fragment2.setRedactorModel(mRedactorModel);
            mRedactorAdapter.addFragment(fragment2);

            PickerRedactorFragment timePickerFragment = new PickerRedactorFragment();
            timePickerFragment.setRedactorModel(mRedactorModel);
            mRedactorAdapter.addFragment(timePickerFragment);
        }
    }

    /**
     * Returns the progress of filling recipe
     *
     * @param currentPosition - position of filling
     * @return progress in %
     */
    private int getProgress(int currentPosition) {
        return (currentPosition + 1) * 100 / mRedactorAdapter.getCount();
    }

    /**
     * Initializing viewpager listener
     */
    public void initListeners() {
        mBinding.recipeVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBinding.redactorProgress.setProgress(getProgress(position));
                String progressText = getString(R.string.step) + " " + (position + 1) +
                        " / " + mRedactorAdapter.getCount();
                mBinding.progressText.setText(progressText);
                if (mRedactorAdapter.getCount() == position + 1) {
                    mBinding.btnSave.setText(getResources().getString(R.string.save));
                } else {
                    mBinding.btnSave.setText(R.string.next);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public Fragment getCurrentFragment() {
        return null;
    }

    @Override
    public ImageView getToolbarImageView() {
        return null;
    }

    @Override
    public AppBarLayout getAppBar() {
        return null;
    }

    public KeyboardView getKeyboard() {
        return mBinding.keyboardView;
    }
}
