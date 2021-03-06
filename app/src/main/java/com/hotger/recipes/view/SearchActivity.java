package com.hotger.recipes.view;

import android.arch.persistence.room.Room;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;

import com.hotger.recipes.App;
import com.hotger.recipes.R;
import com.hotger.recipes.adapter.CardAdapter;
import com.hotger.recipes.databinding.ActivitySearchBinding;
import com.hotger.recipes.database.FirebaseUtils;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.ResponseAPI;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.utils.YummlyAPI;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.view.redactor.BackStackFragment;
import com.hotger.recipes.viewmodel.SearchViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends ControllableActivity {

    private ActivitySearchBinding mBinding;
    private boolean isVisible = true;
    private AppDatabase db;
    private SearchViewModel model;

    /**
     * Result params
     */
    private CardAdapter cardAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "populus-database").allowMainThreadQueries().build();
        model = new SearchViewModel(this, db);
        setSearchAttr();
        setSupportActionBar(mBinding.toolBar);
        setUpNavigation(true);
        addResultsFragment();
    }

    public void setSearchAttr() {
        mBinding.filterBtn.setOnClickListener(v -> slideAnimation(mBinding.filter.filterContainer));
        mBinding.filter.rangeBar.setOnRangeBarChangeListener((rangeBar, leftPinIndex, rightPinIndex, leftPinValue, rightPinValue) -> {
            model.setTimeInMinutes(rightPinValue);
            if (mBinding.filter.checkbox.isChecked()) {
                mBinding.filter.checkbox.setText(rightPinValue);
            }
        });

        mBinding.filter.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                buttonView.setText(mBinding.filter.rangeBar.getRightPinValue());
            } else {
                buttonView.setText("");
            }
        });

        mBinding.searchView.setIconified(true);
        mBinding.searchView.onActionViewExpanded();
        new Handler().postDelayed(() -> mBinding.searchView.clearFocus(), 300);
        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                prepareSearchingAndStart();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        View v = mBinding.searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        v.setBackgroundColor(Color.TRANSPARENT);
        setSpinnerAdapter(mBinding.filter.cuisineSp, model.getCuisines(), YummlyAPI.Description.CUISINE);
        setSpinnerAdapter(mBinding.filter.courseSp, model.getCourse(), YummlyAPI.Description.COURSE);
        setSpinnerAdapter(mBinding.filter.dietSp, model.getDiets(), YummlyAPI.Description.DIET);
        mBinding.filter.searchBtn.setOnClickListener(view -> prepareSearchingAndStart());
    }

    public void slideAnimation(View view) {
        int xDelta = isVisible ? 0 : -view.getWidth();
        int toXDelta = isVisible ? -view.getWidth() : 0;
        TranslateAnimation animate = new TranslateAnimation(xDelta, toXDelta, 0, 0);
        animate.setDuration(200);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (!isVisible) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isVisible) {
                    view.setVisibility(View.GONE);
                }

                isVisible = !isVisible;
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(animate);
    }

    private void setSpinnerAdapter(MaterialSpinner spinner, ArrayList<String> selected, String type) {
        List<Category> course = db.getCategoryDao().getAllCategoriesWithDescription(type);
        ArrayList<String> categories = new ArrayList<>();
        categories.add(getString(R.string.nothing_selected));
        for (Category category : course) {
            categories.add(category.getTitle());
        }

        spinner.setItems(categories);
        spinner.setOnItemSelectedListener((view, position, id, item) -> {
            if (!categories.get(position).equals(getString(R.string.nothing_selected))) {
                addCategory(categories.get(position), selected);
            } else {
               int childCount = mBinding.filter.flowLo.getChildCount();
               for (int i = 0; i < childCount; i++) {
                   TableRow checkbox = (TableRow) mBinding.filter.flowLo.getChildAt(i);
                   if (checkbox == null)
                       continue;
                   if (categories.contains(checkbox.getTag())) {
                       if (mBinding.filter.flowLo.getChildCount() != 1) {
                           ((ViewGroup) checkbox.getParent()).removeView(checkbox);
                           selected.remove(checkbox.getTag());
                       } else {
                           mBinding.filter.flowLo.removeAllViews();
                       }
                   }
               }
            }
        });
    }

    private void addCategory(String category, ArrayList<String> selected) {
        if (selected.contains(category)) {
            return;
        }

        TableRow row = new TableRow(this);
        row.setTag(category);
        row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        row.setPadding(16, 16, 16, 16);

        //create checkbox
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(category);
        checkBox.setBackground(getResources().getDrawable(R.drawable.chekox_shape));
        checkBox.setPadding(30, 0, 30, 0);
        checkBox.setButtonDrawable(null);
        checkBox.setTag(category);
        checkBox.setOnCheckedChangeListener(getCheckedListener(checkBox, selected));
        row.addView(checkBox);
        mBinding.filter.flowLo.addView(row);

        //add category to selected list
        selected.add(category);
    }

    public CompoundButton.OnCheckedChangeListener getCheckedListener(View v, ArrayList<String> selected) {
        return (buttonView, isChecked) -> {
            ((ViewManager) v.getParent()).removeView(v);
            selected.remove(v.getTag());
        };
    }

    public void prepareSearchingAndStart() {
        cardAdapter.clearData();
        mBinding.progress.setVisibility(View.VISIBLE);
        searchRecipe();
        if (mBinding.filter.filterContainer.getVisibility() == View.VISIBLE) {
            slideAnimation(mBinding.filter.filterContainer);
        }
    }

    public void searchRecipe() {
        String searchValue = YummlyAPI.SEARCH + model.getSearchValue(mBinding.filter.checkbox.isChecked());
        App.getApi()
                .search(searchValue)
                .enqueue(new Callback<ResponseAPI<RecipePrev>>() {
                             @Override
                             public void onResponse(Call<ResponseAPI<RecipePrev>> call, Response<ResponseAPI<RecipePrev>> response) {
                                 FirebaseUtils.searchRecipes(model.getCategories(), cardAdapter);
                                 cardAdapter.setData(response.body().getMatches());
                                 mBinding.progress.setVisibility(View.GONE);
                             }

                             @Override
                             public void onFailure(Call<ResponseAPI<RecipePrev>> call, Throwable t) {
                                 Toast.makeText(SearchActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                 mBinding.progress.setVisibility(View.GONE);
                             }
                         }
                );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Fragment curFragment = getCurrentFragment();
                FragmentManager fm = curFragment.getChildFragmentManager();
                if (fm.getBackStackEntryCount() != 0) {
                    fm.popBackStackImmediate();
                    updateCollapsing(mBinding.appbar, false);
                } else {
                    super.onBackPressed();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addResultsFragment() {
        BackStackFragment fragment = new BackStackFragment();
        Bundle recipeBundle = new Bundle();
        recipeBundle.putInt(Utils.EXTRA_NAVIGATION_ID, RecipeListFragment.ID);
        recipeBundle.putBoolean(Utils.IntentVars.NEED_INIT, true);
        fragment.setArguments(recipeBundle);
        updateCollapsing(mBinding.appbar, false);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, RecipeListFragment.class.getName()).commit();
    }

    @Override
    public Fragment getCurrentFragment() {
        return (getSupportFragmentManager().findFragmentByTag(RecipeListFragment.class.getName()));
    }

    public CardAdapter getCardAdapter() {
        return cardAdapter;
    }

    public void setCardAdapter(CardAdapter cardAdapter) {
        this.cardAdapter = cardAdapter;
    }

    @Override
    public ImageView getToolbarImageView() {
        return mBinding.backdrop;
    }

    @Override
    public AppBarLayout getAppBar() {
        return mBinding.appbar;
    }
}
