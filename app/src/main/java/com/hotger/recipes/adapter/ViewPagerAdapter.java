package com.hotger.recipes.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * View pager adapter
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    /**
     * List of fragments
     */
    private final List<Fragment> fragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    /**
     * Add fragment to {@link #fragmentList}
     * @param fragment
     */
    public void addFragment(Fragment fragment) {
        this.fragmentList.add(fragment);
    }
}