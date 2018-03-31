package com.hotger.recipes.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.ViewPagerAdapter;
import com.hotger.recipes.databinding.FragmentProfileBinding;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.BackStackFragment;

public class ProfileFragment extends BackStackFragment {

    private FragmentProfileBinding mBinding;
    private ViewPagerAdapter adapter;
    public static final int MENU_MY_RECIPE_ID = 546489;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        setAdapter();
        mBinding.viewpager.setAdapter(adapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewpager);
        mBinding.tabLayout.getTabAt(0).setText(getResources().getText(R.string.my_recipes));
        mBinding.tabLayout.getTabAt(1).setText(getResources().getText(R.string.my_favs));
        return mBinding.getRoot();
    }

    private void setAdapter() {
        adapter = new ViewPagerAdapter(getChildFragmentManager());

        Fragment recipeFragment = new BackStackFragment();
        Bundle recipesBundle = new Bundle();
        recipesBundle.putInt(Utils.EXTRA_NAVIGATION_ID, MENU_MY_RECIPE_ID);
        recipesBundle.putString(Utils.EXTRA_TYPE, Utils.TYPE.TYPE_MY_RECIPES);
        recipeFragment.setArguments(recipesBundle);
        adapter.addFragment(recipeFragment);

        Fragment favFragment = new BackStackFragment();
        Bundle favBundle = new Bundle();
        favBundle.putInt(Utils.EXTRA_NAVIGATION_ID, MENU_MY_RECIPE_ID);
        favBundle.putString(Utils.EXTRA_TYPE, Utils.TYPE.TYPE_MY_FAVS);
        favFragment.setArguments(favBundle);
        adapter.addFragment(favFragment);
    }
}
