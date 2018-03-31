package com.hotger.recipes.view;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.CardAdapter;
import com.hotger.recipes.database.RelationRecipeTypeViewModel;
import com.hotger.recipes.database.dao.RelationRecipeTypeDao;
import com.hotger.recipes.databinding.FragmentRecipesListBinding;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.MessageModel;
import com.hotger.recipes.utils.ResponseRecipeAPI;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.BackStackFragment;

import java.util.ArrayList;
import java.util.List;

public class RecipeListFragment extends BackStackFragment {

    private FragmentRecipesListBinding mBinding;

    private CardAdapter cardAdapter;

    public static String TAG = "RecipeListFragment";
    public static final int ID = 123456;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cardAdapter = new CardAdapter((ControllableActivity) getActivity(), new ArrayList<>());

        if (getArguments() != null) {
            checkForCategory();
            checkForInit();
            checkForPassingFromApi();
            checkForFavorites();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void checkForFavorites() {
        String navId = getArguments().getString(Utils.EXTRA_TYPE);
        if (navId == null)
            return;

        if (navId.equals(Utils.TYPE.TYPE_MY_RECIPES)) {
            RelationRecipeTypeDao dao = AppDatabase.getDatabase(getActivity()).getRelationRecipeTypeDao();
            cardAdapter.setFromDB(true);
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

    private List<RecipePrev> findFavoritePrevs(List<String> favIds) {
        return AppDatabase.getDatabase(getContext()).getRecipePrevDao().findPrevsFromList(favIds);
    }

    private void checkForPassingFromApi() {
        ResponseRecipeAPI rra = (ResponseRecipeAPI) getArguments().getSerializable(Utils.RECIPE_OBJ);
        if (rra != null) {
            cardAdapter.setData(rra.getMatches());
        }
    }

    private void checkForInit() {
        if (getArguments().getBoolean(Utils.NEED_INIT, false)) {
            ((SearchActivity) getActivity()).setCardAdapter(cardAdapter);
        }
    }

    private void checkForCategory() {
        String searchValue = getArguments().getString(Utils.RECIPE_CATEGORY);
        if (searchValue != null) {
            List<String> ids = AppDatabase.getDatabase(getContext()).getRelationRecipeTypeDao().getRecipesByType(searchValue);
            cardAdapter.setData(AppDatabase.getDatabase(getActivity()).getRecipePrevDao().findPrevsFromList(ids));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipes_list, container, false);
        mBinding.setData(cardAdapter);
        mBinding.setMessageModel(new MessageModel(getString(R.string.favorite_hint), 0, true));
        mBinding.listRv.setAdapter(cardAdapter);
        mBinding.listRv.setLayoutManager(new GridLayoutManager(getContext(), CardAdapter.COLUMNS_COUNT, GridLayoutManager.VERTICAL, false));
        return mBinding.getRoot();
    }
}
