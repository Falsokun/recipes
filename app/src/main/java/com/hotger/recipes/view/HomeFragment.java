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
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.ParseUtils;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.redactor.BackStackFragment;
import com.hotger.recipes.view.redactor.RedactorActivity;

import java.util.List;
import java.util.Random;

public class HomeFragment extends BackStackFragment {

    FragmentHomeBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mBinding.addReceipt.setOnClickListener(view -> startActivity(new Intent(view.getContext(), RedactorActivity.class)));
        mBinding.randomRecipe.setOnClickListener(view -> {
            RecipePrev prev = Utils.getRandomPrev((ControllableActivity) getActivity());
            if (prev.isFromYummly()) {
                ((ControllableActivity) getActivity())
                        .openRecipe(prev.getId(), true);
            } else {
                ((ControllableActivity) getActivity())
                        .openRecipeFromDB(prev.getId(), true);
            }
        });

        mBinding.shoppingList.setOnClickListener(view ->

                startActivity(new Intent(view.getContext(), ShoppingListActivity.class)));
        showInstructions();
        return mBinding.getRoot();
    }

    @Override
    public void showInstructions() {
        Utils.showInstructions(mBinding.inputField, "any text", getActivity(), "10");
    }

    //    public void test() {
//        if (mBinding.recipeText.getText().toString().length() == 0) {
//            Toast.makeText(getContext(), "wrong", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String url = mBinding.recipeText.getText().toString(); //"http://redmond.company/ru/recipes/82/2777/"
//        ParseUtils.parseRecipe(url, getContext(), false);
//    }
}
