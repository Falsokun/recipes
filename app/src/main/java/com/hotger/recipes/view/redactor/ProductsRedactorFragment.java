package com.hotger.recipes.view.redactor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentRedactorProductsBinding;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.MainActivity;
import com.hotger.recipes.viewmodel.RedactorViewModel;

public class ProductsRedactorFragment extends Fragment {

    private RedactorViewModel mRedactorModel;

    private FragmentRedactorProductsBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_redactor_products, container, false);
        mBinding.setModel(mRedactorModel);
        mBinding.listView.setAdapter(mRedactorModel.getAdapter());
        mBinding.productsLineRv.setAdapter(mRedactorModel.getProductsAdapter());
        mBinding.productsLineRv.setLayoutManager(new LinearLayoutManager(getContext()));

        initListeners();
        return mBinding.getRoot();
    }

    private void initListeners() {
        mBinding.addProductName.addTextChangedListener(mRedactorModel.getProductTextChangeListener(mRedactorModel.getAdapter()));
        mBinding.listView.setOnItemClickListener((adapterView, view, i, l) -> {
            mRedactorModel.onHintItemClickAction(view, i, l);
            Utils.hideKeyboard(view);
            mBinding.addProductName.setText("");
        });

        mBinding.addProductName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                int listNum = mBinding.listView.getCount();
                if (listNum == 0) {
                    showAlertDialog();
                }
            }
            return false;
        });
        mBinding.addProductName.setOnFocusChangeListener(mRedactorModel.getOnFocusChangedListener());
    }

    public void setRedactorModel(RedactorViewModel model) {
        mRedactorModel = model;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(getContext().getResources().getString(R.string.no_such_product))
                .setMessage(getContext().getResources().getString(R.string.should_add))
//                .setIcon(R.drawable.ic_android_cat)
                .setCancelable(false)
                .setNegativeButton(getContext().getResources().getString(R.string.no),
                        (dialog, id) -> dialog.cancel())
                .setPositiveButton(getContext().getResources().getString(R.string.yes),
                        (dialog, id) -> {
                            dialog.cancel();
                            addProductToBase();
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addProductToBase() {
        Toast.makeText(getContext(), "add product to base", Toast.LENGTH_SHORT).show();
    }
}
