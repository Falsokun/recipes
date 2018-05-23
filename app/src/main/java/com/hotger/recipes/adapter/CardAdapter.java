package com.hotger.recipes.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hotger.recipes.R;
import com.hotger.recipes.databinding.ItemRecipeBinding;
import com.hotger.recipes.database.FirebaseUtils;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.ControllableActivity;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Displaying recipes list
 *
 * Отображение списка рецептов (превью)
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    public static int COLUMNS_COUNT = 3;

    private ControllableActivity activity;

    private List<RecipePrev> data;

    private ObservableBoolean isEmpty = new ObservableBoolean(false);

    public CardAdapter(ControllableActivity activity, List<RecipePrev> data) {
        this.activity = activity;
        isEmpty.set(data.size() == 0);
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecipePrev recipe = data.get(position);
        final ItemRecipeBinding holderBinding = holder.mBinding;
        holderBinding.recipeName.setText(recipe.getName());
        Glide.with(activity).load(recipe.getImageUrl())
                .apply(new RequestOptions().skipMemoryCache(true))
                .transition(withCrossFade()).into(holderBinding.recipeImg);
        holderBinding.listItem.setOnClickListener(v -> {
            if (recipe.isFromYummly()) {
                List<String> ids = AppDatabase.getDatabase(activity).getRelationRecipeTypeDao().getRecipesById(recipe.getId(), Utils.SP_RECIPES_ID.TYPE_BOOKMARK);
                if (ids.size() != 0) {
                    activity.openRecipeFromDB(recipe.getId());
                } else {
                    activity.openRecipe(recipe.getId());
                }
            } else {
                List<RecipePrev> prev = AppDatabase.getDatabase(activity).getRecipePrevDao().find(recipe.getId());
                if (prev.size() != 0 && !prev.get(0).isFromYummly()) {
                    activity.openRecipeFromDB(recipe.getId());
                } else {
                    FirebaseUtils.getRecipeFromFirebase(recipe.getId(), activity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();// - data.size() % COLUMNS_COUNT;
    }

    public void clearData() {
        data.clear();
        notifyDataSetChanged();
        isEmpty.set(true);
    }

    public void addData(List<RecipePrev> data) {
        this.data.addAll(0, data);
        isEmpty.set(data.size() == 0);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemRecipeBinding mBinding;

        ViewHolder(View view) {
            super(view);
            mBinding = DataBindingUtil.bind(view);
        }
    }

    public void setData(List<RecipePrev> data) {
        this.data = data;
        isEmpty.set(data.size() == 0);
        notifyDataSetChanged();
    }

    public ObservableBoolean getEmpty() {
        return isEmpty;
    }
}
