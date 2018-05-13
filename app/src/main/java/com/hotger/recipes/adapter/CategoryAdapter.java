package com.hotger.recipes.adapter;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hotger.recipes.R;
import com.hotger.recipes.databinding.ItemCategoryBinding;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.MainActivity;
import com.hotger.recipes.view.RecipeListFragment;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Adapter for showing categories in main viewpager
 *
 * Адаптер для отображения категорий в главном меню
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private MainActivity activity;

    /**
     * Adapter's data
     */
    private List<Category> data = new ArrayList<>();

    /**
     * Type of the category {cuisine, course or diet}
     */
    private String type;

    public CategoryAdapter(MainActivity activity, List<Category> data, String type) {
        super();
        this.data = data;
        this.activity = activity;
        this.type = type;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(activity, R.layout.item_category, null);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = getItem(position);
        final ItemCategoryBinding holderBinding = holder.mBinding;
        holderBinding.title.setText(category.getTitle());
        holderBinding.type.setText(category.getType());
        Glide.with(activity).load(category.getUrl())
                .apply(new RequestOptions().skipMemoryCache(true))
                .transition(withCrossFade()).into(holderBinding.bgImage);
        holderBinding.bgImage.setImageURI(Uri.parse(category.getUrl()));
        holderBinding.bgImage.setOnClickListener(v -> chooseCategory(category.getSearchValue()));
    }

    private void chooseCategory(String searchValue) {
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Utils.IntentVars.RECIPE_CATEGORY, searchValue);
        bundle.putSerializable(Utils.IntentVars.RECIPE_TYPE, type);
        fragment.setArguments(bundle);
        activity.setCurrentFragment(fragment, true, RecipeListFragment.class.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private Category getItem(int position) {
        return data.get(position);
    }

    //region Getters and setters
    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        notifyItemRangeRemoved(0, this.data.size() - 1);
        this.data.clear();
        this.data = data;
        notifyItemRangeChanged(0, data.size() - 1);
    }

    public void setType(String type) {
        this.type = type;
    }
    //endregion

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemCategoryBinding mBinding;

        ViewHolder(View view) {
            super(view);
            mBinding = DataBindingUtil.bind(view);
        }
    }
}
