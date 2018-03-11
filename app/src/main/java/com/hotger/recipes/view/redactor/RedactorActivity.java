package com.hotger.recipes.view.redactor;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.ViewPagerAdapter;
import com.hotger.recipes.databinding.ActivityRedactorBinding;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.viewmodel.RedactorViewModel;

public class RedactorActivity extends AppCompatActivity {

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
        mRedactorModel = new RedactorViewModel(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_redactor);
        mBinding.setModel(mRedactorModel);
        initAdapter();
        mBinding.recipeVp.setAdapter(mRedactorAdapter);
        mBinding.redactorProgress.setProgress(getProgress(0));
        initListeners();
        mBinding.btnSave.setOnClickListener(view -> {
            if (mBinding.btnSave.getText()
                    .equals(getResources().getString(R.string.next))) {
                mBinding.recipeVp.setCurrentItem(mBinding.recipeVp.getCurrentItem() + 1);
            } else {
                mRedactorModel.onSave(mBinding.recipeVp);
                mRedactorModel.currentRecipe.log();
            }
        });

        /*int id = getArguments().getInt(Utils.RECIPE_ID, -1);
        if (id != -1) {
            mRedactorModel = new RedactorViewModel(this, id);
        } else {
            mRedactorModel = new RedactorViewModel((MainActivity) getActivity());
        }*/
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

            //TODO:Что-то с этим говном не так
//            TextRedactorFragment fragment2 = new TextRedactorFragment();
//            fragment2.setRedactorModel(mRedactorModel);
//            mRedactorAdapter.addFragment(fragment2);

            NumberPickerFragment cookingTime = new NumberPickerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Utils.STATE, Utils.TIME_PICKER);
            cookingTime.setArguments(bundle);
            cookingTime.setRedactorModel(mRedactorModel);
            mRedactorAdapter.addFragment(cookingTime);

            NumberPickerFragment portions = new NumberPickerFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putInt(Utils.STATE, Utils.NUMBER_PICKER);
            portions.setArguments(bundle1);
            portions.setRedactorModel(mRedactorModel);
            mRedactorAdapter.addFragment(portions);
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
}
