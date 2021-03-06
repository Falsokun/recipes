package com.hotger.recipes.view.redactor;

import android.databinding.DataBindingUtil;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentRedactorProductsBinding;
import com.hotger.recipes.utils.MessageModel;
import com.hotger.recipes.view.ControllableActivity;
import com.hotger.recipes.viewmodel.InputProductsViewModel;

public class ProductsRedactorFragment extends Fragment {

    private InputProductsViewModel inputModel;

    private FragmentRedactorProductsBinding mBinding;

    private MessageModel messageModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_redactor_products, container, false);
        mBinding.setModel(inputModel);
        mBinding.listView.setAdapter(inputModel.getDataHintAdapter());
        mBinding.productsLineRv.setAdapter(inputModel.getProductsAdapter());
        mBinding.setIsEmpty(inputModel.getDataHintAdapter().isEmptyData());
//        KeyboardView keyboard = ((RedactorActivity) getActivity()).getKeyboard();
//        keyboard.setKeyboard(inputModel.getKeyboard(keyboard));
        mBinding.productsLineRv.setLayoutManager(new LinearLayoutManager(getContext()));
        messageModel = new MessageModel((ControllableActivity) getActivity(),
                getString(R.string.add_products_hint), 0, false);
        mBinding.message.btn.setOnClickListener(v -> ((ControllableActivity) getActivity()).openSearch());
        mBinding.setMessageModel(messageModel);
        initListeners();
        return mBinding.getRoot();
    }

    private void initListeners() {
        mBinding.addProductName.addTextChangedListener(inputModel.getProductTextChangeListener());
        mBinding.listView.setOnItemClickListener(inputModel.getOnHintItemClickListener(mBinding.addProductName));
        mBinding.addProductName.setOnEditorActionListener(inputModel.getOnEditorActionListener());
    }

    public void setInputModel(InputProductsViewModel model) {
        inputModel = model;
    }
}
