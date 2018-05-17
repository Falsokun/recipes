package com.hotger.recipes.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.CardAdapter;
import com.hotger.recipes.database.relations.RelationRecipeTypeViewModel;
import com.hotger.recipes.database.dao.RelationRecipeTypeDao;
import com.hotger.recipes.databinding.FragmentRecipesListBinding;
import com.hotger.recipes.database.FirebaseUtils;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.AsyncCalls;
import com.hotger.recipes.utils.MessageModel;
import com.hotger.recipes.utils.ResponseAPI;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.BackStackFragment;

import java.util.ArrayList;
import java.util.List;

public class RecipeListFragment extends BackStackFragment {

    private FragmentRecipesListBinding mBinding;

    private CardAdapter cardAdapter;

    public static final int ID = 123456;

    private MessageModel model;
    private BroadcastReceiver mMessageReceiver;
    private String title = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cardAdapter = new CardAdapter((ControllableActivity) getActivity(), new ArrayList<>());
        model = new MessageModel(getString(R.string.favorite_hint), 0, true);
        mMessageReceiver = getRefreshReceiver();
        if (getArguments() != null) {
            setData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipes_list, container, false);
        mBinding.setData(cardAdapter);
        mBinding.setMessageModel(model);
        mBinding.listRv.setAdapter(cardAdapter);
        mBinding.listRv.setLayoutManager(new GridLayoutManager(getContext(), CardAdapter.COLUMNS_COUNT, GridLayoutManager.VERTICAL, false));

        //may be another way
        if (getArguments() != null && getArguments().getString(Utils.IntentVars.RECIPE_CATEGORY) != null) {
            mBinding.swipeRefresh.setOnRefreshListener(getOnRefreshListener());
            mBinding.swipeRefresh.setEnabled(true);
        } else {
            mBinding.swipeRefresh.setEnabled(false);
        }
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Utils.IntentVars.NEED_INIT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                filter);
    }


    public BroadcastReceiver getRefreshReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setData();
                mBinding.swipeRefresh.setRefreshing(false);
            }
        };
    }

    private void checkForFavorites() {
        String navId = getArguments().getString(Utils.IntentVars.EXTRA_TYPE);
        if (navId == null)
            return;

        if (navId.equals(Utils.SP_RECIPES_ID.TYPE_MY_RECIPES)) {
            RelationRecipeTypeDao dao = AppDatabase.getDatabase(getActivity()).getRelationRecipeTypeDao();
            ViewModelProviders.of(getActivity())
                    .get(RelationRecipeTypeViewModel.class)
                    .getAllPrevs(dao)
                    .observe(getActivity(), recipePrevs -> cardAdapter.setData(findFavoritePrevs(recipePrevs)));
        } else {
            RelationRecipeTypeDao dao = AppDatabase.getDatabase(getActivity()).getRelationRecipeTypeDao();
            ViewModelProviders.of(getActivity())
                    .get(RelationRecipeTypeViewModel.class)
                    .getAllFavs(dao)
                    .observe(getActivity(), favoritePrevs -> cardAdapter.setData(findFavoritePrevs(favoritePrevs)));
        }
    }

    private void setData() {
        checkForSwipe();
        checkForCategory();
        checkForPassingFromApi();
        checkForFavorites();
    }

    private List<RecipePrev> findFavoritePrevs(List<String> favIds) {
        return AppDatabase.getDatabase(getContext()).getRecipePrevDao().findPrevsFromList(favIds);
    }

    private void checkForPassingFromApi() {
        ResponseAPI<RecipePrev> rra = (ResponseAPI<RecipePrev>) getArguments().getSerializable(Utils.IntentVars.RECIPE_OBJ);
        if (rra != null) {
            cardAdapter.setData(rra.getMatches());
            title = getString(R.string.you_can_cook_now);
            ((ControllableActivity) getActivity()).setTitle(title);
        }
    }

    private void checkForSwipe() {
        if (getArguments().getBoolean(Utils.IntentVars.NEED_INIT, false)) {
            ((SearchActivity) getActivity()).setCardAdapter(cardAdapter);
        }
    }

    private void checkForCategory() {
        if (getArguments() == null || getArguments().getString(Utils.IntentVars.RECIPE_CATEGORY) == null)
            return;

        String searchValue = getArguments().getString(Utils.IntentVars.RECIPE_CATEGORY);
        AppDatabase db = AppDatabase.getDatabase(getActivity());
        updateTitle();
        String type = getArguments().getString(Utils.IntentVars.RECIPE_TYPE);
        if (!searchValue.equals(Utils.SP_RECIPES_ID.TYPE_MY_RECIPES)) {
            List<String> ids = db.getRelationRecipeTypeDao().getRecipesByType(searchValue);
            FirebaseUtils.getRecipesByType(searchValue, cardAdapter);
            cardAdapter.setData(db.getRecipePrevDao().findPrevsFromList(ids));
        } else {
            FirebaseUtils.getAllRecipesInCategory(type, cardAdapter);
        }
    }

    private void updateTitle() {
        if (getArguments() == null)
            return;

        String searchValue = getArguments().getString(Utils.IntentVars.RECIPE_CATEGORY);
        if (searchValue != null) {
            title = AppDatabase.getDatabase(getActivity())
                    .getCategoryDao()
                    .getCategoryById(searchValue)
                    .get(0)
                    .getTitle();
            ((ControllableActivity) getActivity())
                    .setTitle(title);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
    }

    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return () -> {
            //if category
            if (getArguments() != null && getArguments().getString(Utils.IntentVars.RECIPE_CATEGORY) != null) {
                String searchValue = getArguments().getString(Utils.IntentVars.RECIPE_CATEGORY);
                cardAdapter.setData(new ArrayList<>());
                AsyncCalls.saveCategoryToDB(getContext(), searchValue, true);
            }
        };
    }

    public String getTitle() {
        return title;
    }
}
