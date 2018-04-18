package com.hotger.recipes.view;

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
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.ResponseRecipeAPI;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.utils.YummlyAPI;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.RecipeNF;
import com.hotger.recipes.view.redactor.BackStackFragment;
import com.hotger.recipes.viewmodel.InputProductsViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FridgeFragment extends BackStackFragment {

    public static final String ID = "fridge-products-id";

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
        mBinding.fragmentRedactor.listView.setAdapter(inputModel.getDataHintAdapter());
        mBinding.fragmentRedactor.productsLineRv.setAdapter(inputModel.getProductsAdapter());
        mBinding.fragmentRedactor.productsLineRv.setLayoutManager(new LinearLayoutManager(getContext()));
        inputModel.getItemTouchListener().attachToRecyclerView(mBinding.fragmentRedactor.productsLineRv);
        initListeners();
        return mBinding.getRoot();
    }

    private void initListeners() {
        mBinding.searchBtn.setOnClickListener(view -> searchForRecipeWithIngridients(inputModel.getProductsAdapter().getData()));
        mBinding.fragmentRedactor.addProductName.addTextChangedListener(inputModel.getProductTextChangeListener());
        mBinding.fragmentRedactor.listView.setOnItemClickListener(inputModel.getOnHintItemClickListener(mBinding.fragmentRedactor.addProductName));
        mBinding.fragmentRedactor.addProductName.setOnEditorActionListener(inputModel.getOnEditorActionListener(mBinding.fragmentRedactor.listView.getCount()));
    }

    @Override
    public void onStart() {
        super.onStart();
        inputModel.restoreListData(getContext(), FridgeFragment.ID);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.fragmentRedactor.inputProducts.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        inputModel.saveListData(getContext(), FridgeFragment.ID);
        super.onStop();
    }

    public void searchForRecipeWithIngridients(List<Product> products) {
        mBinding.fragmentRedactor.inputProducts.setVisibility(View.GONE);
        mBinding.progress.setVisibility(View.VISIBLE);
        ArrayList<String> ingredients = new ArrayList<>();
        for(Product product : products) {
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
                .enqueue(new Callback<ResponseRecipeAPI>() {
                             @Override
                             public void onResponse(Call<ResponseRecipeAPI> call, Response<ResponseRecipeAPI> response) {
                                 Fragment fragment = new BackStackFragment();
                                 Bundle bundle = new Bundle();
                                 bundle.putSerializable(Utils.RECIPE_OBJ, response.body());
                                 if (response.body() != null && response.body().getMatches().size() == 0) {
                                     Toast.makeText(getContext(), "no matches found", Toast.LENGTH_SHORT).show();
                                     mBinding.progress.setVisibility(View.VISIBLE);
                                 } else {
                                     bundle.putInt(Utils.EXTRA_NAVIGATION_ID, RecipeListFragment.ID);
                                     fragment.setArguments(bundle);
                                     ((ControllableActivity) getActivity()).setCurrentFragment(fragment, true, fragment.getTag());
                                 }

                                 mBinding.progress.setVisibility(View.GONE);
                             }

                             @Override
                             public void onFailure(Call<ResponseRecipeAPI> call, Throwable t) {
                                 mBinding.fragmentRedactor.inputProducts.setVisibility(View.VISIBLE);
                                 mBinding.progress.setVisibility(View.GONE);
                             }
                         }
                );
    }
}
