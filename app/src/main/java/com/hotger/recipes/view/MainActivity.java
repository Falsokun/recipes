package com.hotger.recipes.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.hotger.recipes.DisableAppBarLayoutBehavior;
import com.hotger.recipes.R;
import com.hotger.recipes.adapter.ViewPagerAdapter;
import com.hotger.recipes.databinding.ActivityMainBinding;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.BackStackFragment;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;
    ViewPagerAdapter adapter;
    Realm mRealmInstance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mBinding.toolbar);
        setListeners();
        initAdapter();
        initRealm();
        mRealmInstance = Realm.getDefaultInstance();
        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setPagingEnabled(false);
        mBinding.viewPager.setOffscreenPageLimit(3); //to keep fragments in memory
        updateCollapsing(false);
    }

    private void initRealm() {
        Realm.init(this);
        final RealmConfiguration configuration = new RealmConfiguration.Builder().name("recipes.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);
    }

    public void setListeners() {
        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (mBinding.bottomNavigation.getSelectedItemId() == item.getItemId()) {
                return false;
            }

            updateTitle();
            updateCollapsing(false);
            mBinding.viewPager.setCurrentItem(Utils.bottomNavigationTabs.get(item.getItemId()), false);
            return true;
        });
    }

    public void updateCollapsing(boolean enabled) {
        mBinding.appbar.setExpanded(false, false);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mBinding.appbar.getLayoutParams();
        ((DisableAppBarLayoutBehavior) layoutParams.getBehavior()).setEnabled(enabled);
    }

    private void initAdapter() {
        if (adapter == null) {
            adapter = new ViewPagerAdapter(getSupportFragmentManager());

            Fragment homeFragment = new HomeFragment();
            Bundle homeBundle = new Bundle();
            homeBundle.putInt(Utils.EXTRA_NAVIGATION_ID, R.id.menu_home);
            homeFragment.setArguments(homeBundle);
            adapter.addFragment(homeFragment);

            Fragment recipeListFragment = new BackStackFragment();
            Bundle recipeBundle = new Bundle();
            recipeBundle.putInt(Utils.EXTRA_NAVIGATION_ID, R.id.menu_my_recipes);
            recipeListFragment.setArguments(recipeBundle);
            adapter.addFragment(recipeListFragment);

            Fragment redactorFragment = new HomeFragment();
            Bundle redactorBundle = new Bundle();
            redactorBundle.putInt(Utils.EXTRA_NAVIGATION_ID, R.id.menu_other);
            redactorFragment.setArguments(redactorBundle);
            adapter.addFragment(redactorFragment);
        }
    }

    public void updateTitle() {
//        mBinding.toolbar.setTitle(R.string.app_name);
    }

    public Realm getRealmInstance() {
        return mRealmInstance;
    }

    public void updateAdapter() {
        //TODO может оно и не так делается??
        //((RecipeListFragment) adapter.getItem(1)).updateCardAdapterData();
    }

    public void setCurrentFragment(Fragment fragment, boolean addToBackStack, String name) {
        Fragment curFragment = getNavigationFragment(mBinding.viewPager.getCurrentItem());
        FragmentManager fm = curFragment.getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_container, fragment, name);
        if (addToBackStack) {
            ft.addToBackStack(name);
        }

        ft.commit();
        setUpNavigation(addToBackStack);
    }

    public Fragment getNavigationFragment(int position) {
        String name = makeFragmentName(mBinding.viewPager.getId(), position);
        return getSupportFragmentManager().findFragmentByTag(name);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    public void setUpNavigation(boolean value) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(value);
            getSupportActionBar().setDisplayHomeAsUpEnabled(value);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment curFragment = getNavigationFragment(mBinding.viewPager.getCurrentItem());
        FragmentManager fm = curFragment.getChildFragmentManager();
        if (fm.getBackStackEntryCount() != 0) {
            fm.popBackStackImmediate();
            setUpNavigation(false);
            updateCollapsing(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_edit:
                Fragment rootFragment = getNavigationFragment(1);
                if (rootFragment != null) {
                    RecipeFragment recipeFragment = (RecipeFragment) rootFragment
                            .getChildFragmentManager()
                            .findFragmentByTag(RecipeFragment.class.getName());

                    if (recipeFragment != null) {
                        recipeFragment.editRecipe();
                    }
                }

                Toast.makeText(this, "edit2", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setToolbarImage() {
        mBinding.backdrop.setImageDrawable(getResources().getDrawable(R.drawable.rice));
    }

}


