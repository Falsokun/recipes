package com.hotger.recipes.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentHomeBinding;
import com.hotger.recipes.view.redactor.BackStackFragment;
import com.hotger.recipes.view.redactor.RedactorActivity;

public class HomeFragment extends BackStackFragment {

    FragmentHomeBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mBinding.addReceipt.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RedactorActivity.class);
            startActivity(intent);
        });

        return mBinding.getRoot();
    }
}
