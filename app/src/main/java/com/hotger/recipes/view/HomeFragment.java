package com.hotger.recipes.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentHomeBinding;
import com.hotger.recipes.utils.ParseUtils;
import com.hotger.recipes.view.redactor.BackStackFragment;
import com.hotger.recipes.view.redactor.RedactorActivity;

public class HomeFragment extends BackStackFragment {

    FragmentHomeBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mBinding.addReceipt.setOnClickListener(view -> startActivity(new Intent(view.getContext(), RedactorActivity.class)));
        mBinding.randomRecipe.setOnClickListener(view -> test());
        mBinding.shoppingList.setOnClickListener(view -> startActivity(new Intent(view.getContext(), ShoppingListActivity.class)));
        return mBinding.getRoot();
    }

    public void test() {
        if (mBinding.recipeText.getText().toString().length() == 0) {
            Toast.makeText(getContext(), "wrong", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = mBinding.recipeText.getText().toString(); //"http://redmond.company/ru/recipes/82/2777/"
        ParseUtils.parseRecipe(url, getContext(), false);
    }
}
