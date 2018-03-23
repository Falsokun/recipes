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
import com.hotger.recipes.database.RecipePrevViewModel;
import com.hotger.recipes.adapter.CardAdapter;
import com.hotger.recipes.database.RecipePrevDao;
import com.hotger.recipes.databinding.FragmentRecipesListBinding;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.MessageModel;
import com.hotger.recipes.utils.ResponseRecipeAPI;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.BackStackFragment;

import java.util.ArrayList;

public class RecipeListFragment extends BackStackFragment {

    private FragmentRecipesListBinding mBinding;

    private CardAdapter cardAdapter;

    private String type;

    public static String TAG = "RecipeListFragment";
    public static final int ID = 123456;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecipePrevDao dao = AppDatabase.getDatabase(getActivity()).getRecipePrevDao();
        cardAdapter = new CardAdapter((ControllableActivity) getActivity(), new ArrayList<>());

        if (getArguments() != null) {
            checkForCategory();
            checkForInit();
            checkForPassingFromApi();
            checkForFavorites();
        }

        if (type != null)
        ViewModelProviders.of(getActivity())
                .get(RecipePrevViewModel.class)
                .getAllPrevs(dao, type)
                .observe(getActivity(), recipePrevs -> cardAdapter.setData(recipePrevs));
    }

    private void checkForFavorites() {
        int navId = getArguments().getInt(Utils.EXTRA_NAVIGATION_ID, -1);
        if (navId != -1 && navId == R.id.menu_my_recipe) {
            type = Utils.MY_RECIPES;
            cardAdapter.setFromDB(true);
        }
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
            type = searchValue;
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
