package com.hotger.recipes.view.redactor;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hotger.recipes.App;
import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentRedactorNumberPickerBinding;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.viewmodel.RedactorViewModel;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class NumberPickerFragment extends Fragment {

    private final int MAX_PORTIONS = 10;

    /**
     * Redactor view model
     */
    private RedactorViewModel mRedactorModel;

    private FragmentRedactorNumberPickerBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_redactor_number_picker, container, false);
        mBinding.number.setOnValueChangedListener(mRedactorModel.getPickerChangedListener());
        mBinding.number.setValue(mRedactorModel.getCurrentRecipe().getNumberOfServings());
        initNumberPickerValues();
        return mBinding.getRoot();
    }

    private void initNumberPickerValues() {
        mBinding.name.setText(getResources().getString(R.string.persons_number));
        mBinding.number.setMinValue(1);
        mBinding.number.setMaxValue(MAX_PORTIONS);
    }

    public void setRedactorModel(RedactorViewModel mRedactorModel) {
        this.mRedactorModel = mRedactorModel;
    }
}
