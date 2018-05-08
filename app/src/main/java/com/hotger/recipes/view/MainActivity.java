package com.hotger.recipes.view;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.ViewPagerAdapter;
import com.hotger.recipes.databinding.ActivityMainBinding;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.BackStackFragment;
import com.hotger.recipes.view.redactor.RedactorActivity;

public class MainActivity extends ControllableActivity {

    ActivityMainBinding mBinding;
    ViewPagerAdapter adapter;
    BroadcastReceiver mMessageReceiver;

    private String idToOpen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mBinding.toolbar);
        setListeners();
        initAdapter();
        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setPagingEnabled(false);
        mBinding.viewPager.setOffscreenPageLimit(3); //to keep fragments in memory
        Utils.disableShiftMode(mBinding.bottomNavigation);
        updateCollapsing(mBinding.appbar, false);
        mMessageReceiver = getRecipeReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Utils.RECIPE_ID));
    }

    public void setListeners() {
        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (mBinding.bottomNavigation.getSelectedItemId() == item.getItemId()) {
                return false;
            }

            Fragment nextFragment = getNavigationFragment(Utils.bottomNavigationTabs.get(item.getItemId()));
            updateToolbar(nextFragment);
            updateTitle();
            updateCollapsing(mBinding.appbar, false);
            mBinding.viewPager.setCurrentItem(Utils.bottomNavigationTabs.get(item.getItemId()), false);
            return true;
        });
    }

    private void initAdapter() {
        if (adapter == null) {
            adapter = new ViewPagerAdapter(getSupportFragmentManager());

            Fragment homeFragment = new BackStackFragment();
            Bundle homeBundle = new Bundle();
            homeBundle.putInt(Utils.EXTRA_NAVIGATION_ID, R.id.menu_home);
            homeFragment.setArguments(homeBundle);
            adapter.addFragment(homeFragment);

            Fragment recipeListFragment = new BackStackFragment();
            Bundle recipeBundle = new Bundle();
            recipeBundle.putInt(Utils.EXTRA_NAVIGATION_ID, R.id.menu_categories);
            recipeListFragment.setArguments(recipeBundle);
            adapter.addFragment(recipeListFragment);

            Fragment fridgeFragment = new BackStackFragment();
            Bundle fridgeBundle = new Bundle();
            fridgeBundle.putInt(Utils.EXTRA_NAVIGATION_ID, R.id.menu_fridge);
            fridgeFragment.setArguments(fridgeBundle);
            adapter.addFragment(fridgeFragment);

            Fragment recipeFragment = new BackStackFragment();
            Bundle recipesBundle = new Bundle();
            recipesBundle.putInt(Utils.EXTRA_NAVIGATION_ID, R.id.menu_profile);
            recipeFragment.setArguments(recipesBundle);
            adapter.addFragment(recipeFragment);
        }
    }

    public void updateTitle() {
        Fragment curFragment = getNavigationFragment(mBinding.viewPager.getCurrentItem());
        FragmentManager fm = curFragment.getChildFragmentManager();
        String title = "";
        if (fm.getBackStackEntryCount() == 0) {
            title = getTitleByTag(curFragment.getTag());
        }

        mBinding.toolbar.setTitle(title);
    }

    public Fragment getNavigationFragment(int position) {
        String name = makeFragmentName(mBinding.viewPager.getId(), position);
        return getSupportFragmentManager().findFragmentByTag(name);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    @Override
    public void onBackPressed() {
        Fragment curFragment = getNavigationFragment(mBinding.viewPager.getCurrentItem());
        FragmentManager fm = curFragment.getChildFragmentManager();
        if (fm.getBackStackEntryCount() != 0) {
            fm.popBackStackImmediate();
            if (fm.getBackStackEntryCount() == 0) {
                setUpNavigation(false);
            }

            updateCollapsing(mBinding.appbar, false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        menu.removeItem(R.id.menu_edit);
        menu.removeItem(R.id.menu_delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateTitle(String name) {
        mBinding.toolbar.setTitle(name);
    }

    @Override
    public ImageView getToolbarImageView() {
        return mBinding.backdrop;
    }

    @Override
    public AppBarLayout getAppBar() {
        return mBinding.appbar;
    }

    @Override
    public Fragment getCurrentFragment() {
        return getNavigationFragment(mBinding.viewPager.getCurrentItem());
    }

    private void openRedactorFraqment(Context context) {
        Intent intent = new Intent(context, RedactorActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public BroadcastReceiver getRecipeReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Utils.RECIPE_ID)) {
                    Recipe recipe = (Recipe) intent.getSerializableExtra(Utils.RECIPE_ID);
                    idToOpen = recipe.getId();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        openRecipeIfNeeded();
    }

    public void openRecipeIfNeeded() {
        if (idToOpen == null)
            return;
        mBinding.bottomNavigation.setSelectedItemId(R.id.menu_profile);
        ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                "Loading. Please wait...", true);
        dialog.show();
        openRecipeFromDB(idToOpen);
        dialog.cancel();
        idToOpen = null;
    }
}


