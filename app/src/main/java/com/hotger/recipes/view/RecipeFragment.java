package com.hotger.recipes.view;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.hotger.recipes.R;
import com.hotger.recipes.database.RelationCategoryRecipe;
import com.hotger.recipes.database.RelationObj;
import com.hotger.recipes.database.RelationRecipeType;
import com.hotger.recipes.database.dao.FavoritesDao;
import com.hotger.recipes.database.dao.RelationRecipeTypeDao;
import com.hotger.recipes.databinding.FragmentRecipeShowBinding;
import com.hotger.recipes.firebase.FirebaseUtils;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.RedactorActivity;
import com.hotger.recipes.viewmodel.RecipeViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.hotger.recipes.utils.AppDatabase.getDatabase;

public class RecipeFragment extends Fragment {

    private FragmentRecipeShowBinding mBinding;
    private RecipeViewModel model;
    private BroadcastReceiver mMessageReceiver;
    private boolean shouldWait = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new RecipeViewModel((ControllableActivity) getActivity());
        mMessageReceiver = getRecipeReceiver();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_show, container, false);
        mBinding.progress.setVisibility(View.VISIBLE);
        mBinding.container.setVisibility(View.GONE);
        mBinding.setModel(model);
        ((ControllableActivity) getActivity()).updateCollapsing(((ControllableActivity) getActivity()).getAppBar(), true);
        return mBinding.getRoot();
    }

    private void setData() {
        mBinding.products.setAdapter(model.getProductsAdapter());
        if (model.getProductsAdapter().getItemCount() == 0
                && model.getCurrentRecipe().getProducts().size() != 0) {
            model.getProductsAdapter().setData(model.getCurrentRecipe().getProducts());
        }

        mBinding.products.setLayoutManager(new LinearLayoutManager(getContext()));
        initWaveView();
        initHotButtons();
        model.addCategories(mBinding.categoryContainer);
        ((ControllableActivity) getActivity()).setToolbarImage(model.getCurrentRecipe().getImageURL());
        mBinding.progress.setVisibility(View.GONE);
        mBinding.container.setVisibility(View.VISIBLE);
//        ((ControllableActivity) getActivity()).updateTitle(model.getCurrentRecipe().getName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (getArguments() != null
                && getArguments().getSerializable(Utils.RECIPE_OBJ) != null) {
            inflater.inflate(R.menu.menu_fragment, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_edit) {
            Intent intent = new Intent(getActivity(), RedactorActivity.class);
            intent.putExtra(Utils.RECIPE_ID, model.getCurrentRecipe().getId());
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_delete) {
            model.deleteRecipeFromDatabase((ControllableActivity) getActivity(),
                    model.getCurrentRecipe().getId());
            getActivity().onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initWaveView() {
        Recipe recipe = model.getCurrentRecipe();
        if (recipe.getTotalTimeInMinutes() == 0) {
            mBinding.timing.setVisibility(View.GONE);
            mBinding.divider1.setVisibility(View.GONE);
            return;
        }

        mBinding.prepTime.setAnimDuration(3000);
        mBinding.cookingTime.setAnimDuration(3000);
        mBinding.totalTime.setAnimDuration(3000);
        mBinding.prepTime.setProgressValue(recipe.getPrepTimeMinutes());
        mBinding.cookingTime.setProgressValue(recipe.getCookingTimeInMinutes());
        mBinding.totalTime.setProgressValue(recipe.getTotalTimeInMinutes());
        mBinding.prepTime.setCenterTitle(model.getStringTime(recipe.getPrepTimeMinutes()));
        mBinding.cookingTime.setCenterTitle(model.getStringTime(recipe.getCookingTimeInMinutes()));
        mBinding.totalTime.setCenterTitle(model.getStringTime(recipe.getTotalTimeInMinutes()));
    }

    private void initHotButtons() {
        List<RelationRecipeType> type = AppDatabase.getDatabase(getContext())
                .getRelationRecipeTypeDao()
                .getRelation(model.getCurrentRecipe().getId(), Utils.TYPE.TYPE_MY_FAVS);
        if (type.size() != 0) {
            mBinding.favorite.setProgress(1f);
        }

        mBinding.favorite.setOnClickListener(view -> {
            startCheckAnimation(mBinding.favorite);
            checkFavorites(!(mBinding.favorite.getProgress() == 0f), model.getCurrentRecipe().getId());
        });

        mBinding.bookmark.setOnClickListener(view -> startCheckAnimation(mBinding.bookmark));
    }

    private void checkFavorites(boolean isAlreadyInset, String id) {
        RelationRecipeTypeDao dao = getDatabase(getContext()).getRelationRecipeTypeDao();
        if (!isAlreadyInset) {
            dao.insert(new RelationRecipeType(id, Utils.TYPE.TYPE_MY_FAVS));
        } else {
            dao.delete(new RelationRecipeType(id, Utils.TYPE.TYPE_MY_FAVS));
        }
    }

    private void startCheckAnimation(LottieAnimationView animationView) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(1000);
        animator.addUpdateListener(valueAnimator -> animationView.setProgress((Float) valueAnimator.getAnimatedValue()));

        if (animationView.getProgress() == 0f) {
            animator.start();
        } else {
            animationView.setProgress(0f);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        model.OnResume();
        IntentFilter filter = new IntentFilter(Utils.RECIPE_OBJ);
        filter.addAction(FirebaseUtils.RECIPES_REF);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                filter);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    public BroadcastReceiver getRecipeReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Utils.RECIPE_OBJ:
                        Recipe recipe = (Recipe) intent.getSerializableExtra(Utils.RECIPE_OBJ);
                        model.setCurrentRecipe(recipe);
                        if (!intent.getBooleanExtra(FirebaseUtils.RECIPES_REF, false)) {
                            setData();
                        } else {
                            shouldWait = true;
                        }

                        break;
                    case FirebaseUtils.RECIPES_REF:
                        if (intent.getBooleanExtra(FirebaseUtils.CATEGORY_REF_SEND, false)) {
                            List<RelationCategoryRecipe> arr =
                                    (List<RelationCategoryRecipe>) intent.getSerializableExtra(FirebaseUtils.RECIPE_REF_EXTRA);
                            ArrayList<String> ids = new ArrayList<>();
                            for (RelationCategoryRecipe rel : arr) {
                                ids.add(rel.getCategoryId());
                            }

                            List<Category> categories  = AppDatabase
                                    .getDatabase(context)
                                    .getCategoryDao()
                                    .getCategoriesWithIds(ids);
                            model.getCurrentRecipe().setCategories(categories);
                        } else {
                            List<Product> arr = (List<Product>) intent.getSerializableExtra(FirebaseUtils.RECIPE_REF_EXTRA);
                            model.getCurrentRecipe().setProducts(arr);
                        }

                        if (!shouldWait) {
                            setData();
                        } else {
                            shouldWait = false;
                        }
                        break;
                }
            }
        };
    }
}