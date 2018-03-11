package com.hotger.recipes.view.redactor;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.CategoryFragment;
import com.hotger.recipes.view.FridgeFragment;
import com.hotger.recipes.view.HomeFragment;
import com.hotger.recipes.view.MainActivity;
import com.hotger.recipes.view.RecipeListFragment;

public class BackStackFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backstack, container, false);
        replaceFragment();
        return view;
    }

    private void replaceFragment() {
        if (getChildFragmentManager().getBackStackEntryCount() != 0) {
            return;
        }

        int navigationId = getArguments().getInt(Utils.EXTRA_NAVIGATION_ID, -1);
        if (navigationId == -1) {
            return;
        }

        Fragment rootFragment = null;
        switch (navigationId) {
            case R.id.menu_categories:
                rootFragment = new CategoryFragment();
                break;
            case R.id.menu_home:
                rootFragment = new HomeFragment();
                break;
            case R.id.menu_fridge:
                rootFragment = new FridgeFragment();
                ((FridgeFragment) rootFragment)
                        .setInputModel(Utils.getInputProductsModel((MainActivity) getActivity()));
                break;
            case RecipeListFragment.ID:
                rootFragment = new RecipeListFragment();
                rootFragment.setArguments(getArguments());
                break;
            default:
                break;
        }

        if (rootFragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,
                            rootFragment,
                            rootFragment.getClass().getName())
                    .commit();
        }
    }
}
