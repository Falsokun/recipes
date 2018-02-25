package com.hotger.recipes.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.CardAdapter;
import com.hotger.recipes.databinding.FragmentRecipesListBinding;
import com.hotger.recipes.utils.Recipe;
import com.hotger.recipes.view.redactor.BackStackFragment;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.Sort;

public class RecipeListFragment extends BackStackFragment {

    private FragmentRecipesListBinding mBinding;

    private CardAdapter cardAdapter;

    private Realm realmInstance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realmInstance = ((MainActivity) getActivity()).getRealmInstance();
        cardAdapter = new CardAdapter((MainActivity) getActivity(), realmInstance.where(Recipe.class).findAllSorted("id", Sort.DESCENDING));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipes_list, container, false);
        mBinding.setData(cardAdapter);
        mBinding.listRv.setAdapter(cardAdapter);
        mBinding.listRv.setLayoutManager(new LinearLayoutManager(getContext()));
        return mBinding.getRoot();
    }

    public void updateCardAdapterData() {
        OrderedRealmCollection<Recipe> data = realmInstance.where(Recipe.class).findAllSorted("id", Sort.DESCENDING);
        cardAdapter.updateData(data);
    }
}
