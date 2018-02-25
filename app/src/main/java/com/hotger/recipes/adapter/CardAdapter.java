package com.hotger.recipes.adapter;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.ItemRecipeBinding;
import com.hotger.recipes.utils.Recipe;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.MainActivity;
import com.hotger.recipes.view.RecipeFragment;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class CardAdapter extends RealmRecyclerViewAdapter<Recipe, RecyclerView.ViewHolder> {

    private MainActivity activity;

    public CardAdapter(MainActivity activity, OrderedRealmCollection<Recipe> data) {
        super(data, true, true);
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Recipe recipe = getItem(position);
        final ItemRecipeBinding holderBinding = ((ViewHolder) holder).mBinding;
        holderBinding.recipeName.setText(recipe.getTitle());
        holderBinding.listItem.setOnClickListener(v -> {
            openFragmentWithRecipe(recipe);
        });
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ItemRecipeBinding mBinding;

        ViewHolder(View view) {
            super(view);
            mBinding = DataBindingUtil.bind(view);
        }
    }

    public boolean isEmpty() {
        return getData().isEmpty();
    }

    private void openFragmentWithRecipe(Recipe recipe) {
        Fragment fragment = new RecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Utils.RECIPE_ID, (int) recipe.getId());
        fragment.setArguments(bundle);
        activity.setCurrentFragment(fragment, true, RecipeFragment.class.getName());
    }
}
