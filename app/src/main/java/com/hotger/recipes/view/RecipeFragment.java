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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.hotger.recipes.R;
import com.hotger.recipes.database.RelationCategoryRecipe;
import com.hotger.recipes.database.RelationRecipeType;
import com.hotger.recipes.database.dao.RelationRecipeTypeDao;
import com.hotger.recipes.databinding.FragmentRecipeShowBinding;
import com.hotger.recipes.firebase.FirebaseUtils;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.model.Ingredient;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.RecipeUtils;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.RedactorActivity;
import com.hotger.recipes.viewmodel.RecipeViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hotger.recipes.utils.AppDatabase.getDatabase;

public class RecipeFragment extends Fragment {

    private FragmentRecipeShowBinding mBinding;
    private RecipeViewModel model;
    private BroadcastReceiver mMessageReceiver;
    private boolean shouldWait = false;
    private boolean shouldShowOptions = false;

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
        mBinding.minus.setOnClickListener(model.getOnClickListener(mBinding.portionsValue, false));
        mBinding.plus.setOnClickListener(model.getOnClickListener(mBinding.portionsValue, true));
        ((ControllableActivity) getActivity()).updateCollapsing(((ControllableActivity) getActivity()).getAppBar(), true);
        checkForPassingFromDB();
        return mBinding.getRoot();
    }

    private void checkForPassingFromDB() {
        if (getArguments() != null && getArguments().getSerializable(Utils.RECIPE_OBJ) != null) {
            model.setCurrentRecipe((Recipe) getArguments().getSerializable(Utils.RECIPE_OBJ));
            setData();
            shouldShowOptions = true;
            getActivity().invalidateOptionsMenu();
        }
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
        mBinding.portionsValue.setText(model.getCurrentRecipe().getStringPortions());
        mBinding.progress.setVisibility(View.GONE);
        mBinding.container.setVisibility(View.VISIBLE);
//        ((ControllableActivity) getActivity()).updateTitle(model.getCurrentRecipe().getName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (shouldShowOptions) {
            inflater.inflate(R.menu.menu_fragment, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);
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

        type = AppDatabase.getDatabase(getContext())
                .getRelationRecipeTypeDao()
                .getRelation(model.getCurrentRecipe().getId(), Utils.TYPE.TYPE_BOOKMARK);
        if (type.size() != 0) {
            mBinding.bookmark.setProgress(1f);
        }

        mBinding.favorite.setOnClickListener(view -> {
            startCheckAnimation(mBinding.favorite);
            checkFavorites(!(mBinding.favorite.getProgress() == 0f), model.getCurrentRecipe().getId());
        });

        mBinding.bookmark.setOnClickListener(view -> {
            checkSavedRecipes(!(mBinding.bookmark.getProgress() == 0f), model.getCurrentRecipe());
            startCheckAnimation(mBinding.bookmark);
        });
    }

    private void checkSavedRecipes(boolean isAlreadyInset, Recipe recipe) {
        //RelationRecipeTypeDao dao = getDatabase(getContext()).getRelationRecipeTypeDao();
        if (!isAlreadyInset) {
            //dao.insert(new RelationRecipeType(recipe.getId(), Utils.TYPE.TYPE_BOOKMARK));
            RecipeUtils.saveToDatabase(recipe, (ControllableActivity) getActivity(), false, null, Utils.TYPE.TYPE_BOOKMARK, true);
        } else {
            //dao.delete(new RelationRecipeType(recipe.getId(), Utils.TYPE.TYPE_BOOKMARK));
            RecipeUtils.deleteBookmarkFromDatabase(recipe, (ControllableActivity) getActivity(), false);
        }
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

    public void prepareProducts(Recipe recipe) {
        AppDatabase db = AppDatabase.getDatabase(getContext());
        Product p;
        for (String string : recipe.getIngredientLines()) {
            p = new Product();
            List<Ingredient> list = db.getIngredientDao().getIngredientLike(string);
            if (list.size() == 0)
                continue;
            p.setIngredientId(list.get(0).getEn());
            p.setAmount(getIngredientsAmount(string));
            recipe.getProducts().add(p);
        }
    }

    public double getIngredientsAmount(String line) {
        line = line.toLowerCase();
        line = line.replaceAll("^(,| )+", "");
        Pattern p = Pattern.compile("((?:\\d+ )?\\d+(?:(?:,|.|/)\\d+)?)");
        Matcher m = p.matcher(line);
        if (!m.find()) {
            return 0;
        }

        String amount = m.group(1);
        if (amount.contains(" ")) {
            return
        }
        if (amount.contains("/")) {
            DecimalFormat df = new DecimalFormat("#.##");
            double d = Double.valueOf(amount.split("/")[0]) / Double.valueOf(amount.split("/")[1]);
            return Double.valueOf(df.format(d));
        }

        if (amount.contains(",")) {
            String temp = amount.replace(",", ".");
            return Double.valueOf(temp);
        }

        return Double.parseDouble(amount);
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
                            prepareProducts(recipe);
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