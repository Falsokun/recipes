package com.hotger.recipes.adapter;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hotger.recipes.R;
import com.hotger.recipes.databinding.ItemCategoryBinding;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.MainActivity;
import com.hotger.recipes.view.RecipeListFragment;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categoryList = new ArrayList<>();
    private MainActivity activity;
    private String type;

    public CategoryAdapter(MainActivity activity, List<Category> categoryList, String type) {
        super();
        this.categoryList = categoryList;
        this.activity = activity;
        this.type = type;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = View.inflate(activity, R.layout.item_category, null);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = getItem(position);
        final ItemCategoryBinding holderBinding = holder.mBinding;
        holderBinding.title.setText(category.getTitle());
        holderBinding.type.setText(category.getType());
        Glide.with(activity).load(category.getUrl()).transition(withCrossFade()).into(holderBinding.bgImage);
        holderBinding.bgImage.setImageURI(Uri.parse(category.getUrl()));
        holderBinding.bgImage.setOnClickListener(v -> changeCategory(category.getSearchValue()));
    }

    private void changeCategory(String searchValue) {
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Utils.RECIPE_CATEGORY, searchValue);
        bundle.putSerializable(Utils.RECIPE_TYPE, type);
        fragment.setArguments(bundle);
        activity.setCurrentFragment(fragment, true, fragment.getTag());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    private Category getItem(int position) {
        return categoryList.get(position);
    }

    public List<Category> getData() {
        return categoryList;
    }


    public boolean isEmpty() {
        return getData().isEmpty();
    }

    public void setData(List<Category> data) {
        this.categoryList = data;
        notifyDataSetChanged();
    }

    public void setType(String type) {
        this.type = type;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemCategoryBinding mBinding;

        ViewHolder(View view) {
            super(view);
            mBinding = DataBindingUtil.bind(view);
        }
    }
}
