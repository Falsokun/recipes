package com.hotger.recipes.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.hotger.recipes.R;
import com.hotger.recipes.database.relations.RelationCategoryRecipe;
import com.hotger.recipes.database.relations.RelationRecipeType;
import com.hotger.recipes.database.dao.RelationRecipeTypeDao;
import com.hotger.recipes.databinding.FragmentRecipeShowBinding;
import com.hotger.recipes.database.FirebaseUtils;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.UI.EstimatesDialog;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.RecipeUtils;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.utils.YummlyAPI;
import com.hotger.recipes.view.redactor.RedactorActivity;
import com.hotger.recipes.viewmodel.RecipeViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.hotger.recipes.utils.AppDatabase.getDatabase;

public class RecipeFragment extends Fragment {

    static final int MIN_DISTANCE = 300;
    private float downX,upX;

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
        mBinding.nutritionEstimates.setOnClickListener(v -> new EstimatesDialog(getContext(),
                model.getCurrentRecipe().getNutritionEstimates()).show());
        if (getArguments() != null
                && getArguments().getBoolean(Utils.IntentVars.INIT_GESTURES, false)) {
            mBinding.scrollView.setOnTouchListener(getOnTouchListener());
        }

        ((ControllableActivity) getActivity()).updateCollapsing(((ControllableActivity) getActivity()).getAppBar(), true);
        checkForPassingFromDB();
        return mBinding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private View.OnTouchListener getOnTouchListener() {
        return (v, event) -> {
            if (isSwipe(event)) {
                Toast.makeText(getContext(), "reload", Toast.LENGTH_SHORT).show();
                openRandomRecipe();
                return true;
            }

            return false;
        };
    }

    private void openRandomRecipe() {
        mBinding.progress.setVisibility(View.VISIBLE);
        mBinding.container.setVisibility(View.GONE);
        RecipePrev prev = Utils.getRandomPrev((ControllableActivity) getActivity());
        if (prev.isFromYummly()) {
            ((ControllableActivity) getActivity()).loadRecipe(prev.getId());
        } else {
            ((ControllableActivity) getActivity()).loadRecipeFromDB(prev.getId());
        }
    }

    private boolean isSwipe(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
            }

            case MotionEvent.ACTION_UP: {
                upX = event.getX();

                float deltaX = downX - upX;

                // swipe horizontal
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        Toast.makeText(getContext(), "left to right", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if (deltaX > 0) {
                        Toast.makeText(getContext(), "right to left", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void checkForPassingFromDB() {
        if (getArguments() != null && getArguments().getSerializable(Utils.IntentVars.RECIPE_OBJ) != null) {
            model.setCurrentRecipe((Recipe) getArguments().getSerializable(Utils.IntentVars.RECIPE_OBJ));
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
        mBinding.recipeSteps.setText(Html.fromHtml(model.getCurrentRecipe().getPreparations()));
        ((ControllableActivity) getActivity()).setTitle(model.getCurrentRecipe().getName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_recipe, menu);
        menu.removeItem(R.id.menu_search);
        if (!shouldShowOptions) {
            menu.removeItem(R.id.menu_delete);
            menu.removeItem(R.id.menu_edit);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                Intent intent = new Intent(getActivity(), RedactorActivity.class);
                intent.putExtra(Utils.IntentVars.RECIPE_ID, model.getCurrentRecipe().getId());
                intent.putExtra(Utils.IntentVars.SHOULD_OPEN_RECIPE, false);
                startActivity(intent);
                break;
            case R.id.menu_delete:
                model.deleteRecipeFromDatabase((ControllableActivity) getActivity(),
                        model.getCurrentRecipe().getId());
                getActivity().onBackPressed();
                break;
            case R.id.menu_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, model.getCurrentRecipe().asString(getContext()));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            default:
                break;
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
                .getRelation(model.getCurrentRecipe().getId(), Utils.SP_RECIPES_ID.TYPE_MY_FAVS);
        if (type.size() != 0) {
            mBinding.favorite.setProgress(1f);
        }

        type = AppDatabase.getDatabase(getContext())
                .getRelationRecipeTypeDao()
                .getRelation(model.getCurrentRecipe().getId(), Utils.SP_RECIPES_ID.TYPE_BOOKMARK);
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
        if (!isAlreadyInset) {
            RecipeUtils.saveToDatabase(recipe, (ControllableActivity) getActivity(), false,
                    null, Utils.SP_RECIPES_ID.TYPE_BOOKMARK, true);
        } else {
            RecipeUtils.deleteBookmarkFromDatabase(recipe,
                    (ControllableActivity) getActivity(), false);
        }
    }

    private void checkFavorites(boolean isAlreadyInset, String id) {
        RelationRecipeTypeDao dao = getDatabase(getContext()).getRelationRecipeTypeDao();
        if (!isAlreadyInset) {
            dao.insert(new RelationRecipeType(id, Utils.SP_RECIPES_ID.TYPE_MY_FAVS));
        } else {
            dao.delete(new RelationRecipeType(id, Utils.SP_RECIPES_ID.TYPE_MY_FAVS));
        }
    }

    private void startCheckAnimation(LottieAnimationView animationView) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(1000);
        animator.addUpdateListener(valueAnimator ->
                animationView.setProgress((Float) valueAnimator.getAnimatedValue()));

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
        IntentFilter filter = new IntentFilter(Utils.IntentVars.RECIPE_OBJ);
        filter.addAction(FirebaseUtils.RECIPES_REF);
        filter.addAction(YummlyAPI.REC_DIRECTIONS);
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
                    case Utils.IntentVars.RECIPE_OBJ:
                        Recipe recipe = (Recipe) intent.getSerializableExtra(Utils.IntentVars.RECIPE_OBJ);
                        model.setCurrentRecipe(recipe);
                        if (intent.getBooleanExtra(Utils.IntentVars.SHOULD_WAIT, true)) {
                            shouldWait = true;
                            if (!intent.getBooleanExtra(FirebaseUtils.RECIPES_REF, false)) {
                                model.prepareProducts(recipe, getContext());
                            }
                        } else {
                            setData();
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

                            List<Category> categories = AppDatabase
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

                    case YummlyAPI.REC_DIRECTIONS:
                        String instructions = intent.getStringExtra(YummlyAPI.REC_DIRECTIONS);
                        model.setInstructions(instructions);
                        shouldWait = false;
                        setData();
                        break;
                }
            }
        };
    }

    public String getTitle() {
        if (model != null && model.getCurrentRecipe() != null) {
            return model.getCurrentRecipe().getName();
        } else {
            return getString(R.string.app_name);
        }
    }
}