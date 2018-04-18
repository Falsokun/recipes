package com.hotger.recipes.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hotger.recipes.App;
import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentHomeBinding;
import com.hotger.recipes.model.Ingredient;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.ResponseRecipeAPI;
import com.hotger.recipes.utils.TranslateAPI;
import com.hotger.recipes.utils.YummlyAPI;
import com.hotger.recipes.view.redactor.BackStackFragment;
import com.hotger.recipes.view.redactor.RedactorActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        AppDatabase db = AppDatabase.getDatabase(getContext());
        List<Ingredient> list = db.getIngredientDao().getIngredientLike("1/2 milk");
        String s = list.get(0).getEn();
    }
}
