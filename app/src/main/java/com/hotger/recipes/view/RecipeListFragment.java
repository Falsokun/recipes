package com.hotger.recipes.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.CardAdapter;
import com.hotger.recipes.databinding.FragmentRecipesListBinding;
import com.hotger.recipes.utils.ResponseRecipeAPI;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.BackStackFragment;

import java.util.ArrayList;

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
            String searchValue = getArguments().getString(Utils.RECIPE_CATEGORY);
            if (searchValue != null) {
                showListFromDB(searchValue);
            }

            if (getArguments().getBoolean(Utils.NEED_INIT, false)) {
                ((SearchActivity) getActivity()).setCardAdapter(cardAdapter);
            }

            ResponseRecipeAPI rra = (ResponseRecipeAPI) getArguments().getSerializable(Utils.RECIPE_OBJ);
            if (rra != null) {
                cardAdapter.setData(rra.getMatches());
            }
        }
    }

    private void showListFromDB(String searchValue) {
        cardAdapter = new CardAdapter((ControllableActivity) getActivity(),
                ((ControllableActivity) getActivity()).getDatabase().getRecipePrevDao().getRecipesByType(searchValue));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipes_list, container, false);
        mBinding.setData(cardAdapter);
        mBinding.listRv.setAdapter(cardAdapter);
        mBinding.listRv.setLayoutManager(new GridLayoutManager(getContext(), CardAdapter.COLUMNS_COUNT, GridLayoutManager.VERTICAL, false));
        return mBinding.getRoot();
    }
}
