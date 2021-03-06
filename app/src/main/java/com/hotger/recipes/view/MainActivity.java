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
import android.widget.Toast;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.ViewPagerAdapter;
import com.hotger.recipes.databinding.ActivityMainBinding;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.BackStackFragment;

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
                new IntentFilter(Utils.IntentVars.RECIPE_ID));
        checkForSending();
    }

    private void checkForSending() {
        if (getIntent() == null)
            return;

        String str = getIntent().getStringExtra(Utils.IntentVars.SHARE_TEXT);
        if (str != null) {
            if (Utils.matchesUrl(str)) {
            } else {
                Toast.makeText(this, "plain text", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setListeners() {
        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (mBinding.bottomNavigation.getSelectedItemId() == item.getItemId()) {
                return false;
            }

            Fragment nextFragment = getNavigationFragment(Utils.bottomNavigationTabs.get(item.getItemId()));
            updateToolbar(nextFragment);
            updateCollapsing(mBinding.appbar, false);
            mBinding.viewPager.setCurrentItem(Utils.bottomNavigationTabs.get(item.getItemId()), false);
            updateOptions();
            updateTitle();
            invalidateOptionsMenu(); //or respectively its support method.
            showInstructions();
            return true;
        });
    }

    private void updateOptions() {
        for (int i = 0; i < adapter.getCount(); i++) {
            adapter.getItem(i).setHasOptionsMenu(i == mBinding.viewPager.getCurrentItem());
            FragmentManager fm = adapter.getItem(i).getChildFragmentManager();
            if (fm.getBackStackEntryCount() != 0) {
                String name = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
                Fragment childFragment = fm.findFragmentByTag(name);
                if (childFragment != null) {
                    childFragment.setHasOptionsMenu(i == mBinding.viewPager.getCurrentItem());
                }
            }

        }
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
        getSupportActionBar().setTitle(getStringTitle());
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

            updateTitle();
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

    @Override
    public ImageView getToolbarImageView() {
        return mBinding.backdrop;
    }

    @Override
    public AppBarLayout getAppBar() {
        return mBinding.appbar;
    }

    public String getStringTitle() {
        Fragment curFragment = getNavigationFragment(mBinding.viewPager.getCurrentItem());
        FragmentManager fm = curFragment.getChildFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            return getTitleByNum(mBinding.viewPager.getCurrentItem());
        } else {
            String name = fm
                    .getBackStackEntryAt(fm.getBackStackEntryCount() - 1)
                    .getName();
            if (name == null)
                return getString(R.string.app_name);

            Fragment visibleFragment;
            if (name.equals(RecipeListFragment.class.getName())) {
                visibleFragment = fm.findFragmentByTag(name);
                return ((RecipeListFragment) visibleFragment).getTitle();
            } else {
                if (name.equals(RecipeFragment.class.getName())) {
                    Fragment fragment = fm.findFragmentById(R.id.fragment_container);
                    if (fragment instanceof RecipeFragment) {
                        return ((RecipeFragment) fragment).getTitle();
                    }
                }
            }

            return name;
        }
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
        getWindow().getDecorView().requestLayout();
    }

    private String getTitleByNum(int currentItem) {
        switch (currentItem) {
            case 0:
                return getString(R.string.app_name);
            case 1:
                return getString(R.string.categories);
            case 2:
                return getString(R.string.fridge);
            default:
                return getString(R.string.my_recipes);
        }
    }

    @Override
    public Fragment getCurrentFragment() {
        return getNavigationFragment(mBinding.viewPager.getCurrentItem());
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
                if (intent.getAction().equals(Utils.IntentVars.RECIPE_ID)) {
                    Recipe recipe = (Recipe) intent.getSerializableExtra(Utils.IntentVars.RECIPE_ID);
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

    public void showInstructions() {
        int id = mBinding.viewPager.getCurrentItem();
        BackStackFragment navFragment = (BackStackFragment)getNavigationFragment(id);
        Fragment container = navFragment.getChildFragmentManager().findFragmentById(R.id.fragment_container);
        if (container instanceof CategoryFragment) {
            ((CategoryFragment)container).showInstructions();
        } else if (container instanceof FridgeFragment) {
            ((FridgeFragment)container).showInstructions();
        } if (container instanceof HomeFragment) {
            ((HomeFragment)container).showInstructions();
        }
    }
}


