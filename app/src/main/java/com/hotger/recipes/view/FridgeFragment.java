package com.hotger.recipes.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hotger.recipes.App;
import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentFridgeBinding;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.utils.ResponseAPI;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.utils.YummlyAPI;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.view.redactor.BackStackFragment;
import com.hotger.recipes.viewmodel.InputProductsViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FridgeFragment extends BackStackFragment {

    FragmentFridgeBinding mBinding;
    InputProductsViewModel inputModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fridge, container, false);
        inputModel = new InputProductsViewModel((ControllableActivity) getActivity(), new ArrayList<>(), true, false, false);
        mBinding.setModel(inputModel);
        mBinding.fragmentRedactor.setIsEmpty(inputModel.getDataHintAdapter().isEmptyData());
        mBinding.fragmentRedactor.listView.setAdapter(inputModel.getDataHintAdapter());
        mBinding.fragmentRedactor.productsLineRv.setAdapter(inputModel.getProductsAdapter());
        mBinding.fragmentRedactor.productsLineRv.setLayoutManager(new LinearLayoutManager(getContext()));
        inputModel.getItemTouchListener(Utils.SP_RECIPES_ID.TYPE_FRIDGE_ID)
                .attachToRecyclerView(mBinding.fragmentRedactor.productsLineRv);
        initListeners();
        return mBinding.getRoot();
    }

    private void initListeners() {
        mBinding.searchBtn.setOnClickListener(view -> searchForRecipeWithIngridients(inputModel.getProductsAdapter().getData()));
        mBinding.fragmentRedactor.addProductName.addTextChangedListener(inputModel.getProductTextChangeListener());
        mBinding.fragmentRedactor.listView.setOnItemClickListener(inputModel.getOnHintItemClickListener(mBinding.fragmentRedactor.addProductName));
    }

    @Override
    public void onStart() {
        super.onStart();
        inputModel.restoreListData(getContext(), Utils.SP_RECIPES_ID.TYPE_FRIDGE_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.fragmentRedactor.inputProducts.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void searchForRecipeWithIngridients(List<Product> products) {
        mBinding.fragmentRedactor.inputProducts.setVisibility(View.GONE);
        mBinding.progress.setVisibility(View.VISIBLE);
        ArrayList<String> ingredients = new ArrayList<>();
        for (Product product : products) {
            ingredients.add(product.getIngredientId());
        }

        StringBuilder builder = new StringBuilder();
        builder.append(YummlyAPI.SEARCH);
        for (String ingredient : ingredients) {
            builder.append("&allowedIngredient[]=");
            builder.append(ingredient);
        }

        builder.append("&maxResult=" + YummlyAPI.MAX_RESULT);

        App.getApi()
                .search(builder.toString())
                .enqueue(new Callback<ResponseAPI<RecipePrev>>() {
                    @Override
                    public void onResponse(Call<ResponseAPI<RecipePrev>> call,
                                           Response<ResponseAPI<RecipePrev>> response) {
                        Fragment fragment = new BackStackFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Utils.IntentVars.RECIPE_OBJ, response.body());
                        if (response.body() != null && response.body().getMatches().size() == 0) {
                            Toast.makeText(getContext(), "no matches found", Toast.LENGTH_SHORT).show();
                            mBinding.progress.setVisibility(View.VISIBLE);
                        } else {
                            bundle.putInt(Utils.EXTRA_NAVIGATION_ID, RecipeListFragment.ID);
                            fragment.setArguments(bundle);
                            ((ControllableActivity) getActivity())
                                    .setCurrentFragment(fragment, true,
                                            RecipeListFragment.class.getName());
                        }

                        mBinding.progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<ResponseAPI<RecipePrev>> call, Throwable t) {
                        mBinding.fragmentRedactor.inputProducts.setVisibility(View.VISIBLE);
                        mBinding.progress.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void showInstructions() {
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (!pref.getBoolean(Utils.SharedPref.FRIDGE_PREF, false)) {
            pref.edit().putBoolean(Utils.SharedPref.FRIDGE_PREF, true).apply();
            View show = mBinding.fragmentRedactor.addProductName;
            Utils.showInstructions(show, getString(R.string.products_filter), getString(R.string.products_filter_hint), getActivity(),
                    String.valueOf(show.getId()));
        }
    }
}