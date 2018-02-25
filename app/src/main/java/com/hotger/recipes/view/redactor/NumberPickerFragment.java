package com.hotger.recipes.view.redactor;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentRedactorNumberPickerBinding;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.viewmodel.RedactorViewModel;

public class NumberPickerFragment extends Fragment {

    private final int MAX_HOURS = 10;
    private final int MAX_MINUTES = 59;
    private final int MAX_PORTIONS = 10;

    /**
     * Redactor view model
     */
    private RedactorViewModel mRedactorModel;

    /**
     * Ovservable field
     */
    private ObservableBoolean isTimePicker;

    private FragmentRedactorNumberPickerBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTimePicker = new ObservableBoolean(getArguments().getInt(Utils.STATE) == Utils.TIME_PICKER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_redactor_number_picker, container, false);
        mBinding.firstNumber.setOnValueChangedListener(mRedactorModel.getPickerChangedListener(isTimePicker.get(), true));
        mBinding.setIsTimePicker(isTimePicker);
        if (isTimePicker.get()) {
            initTimePickerValues();
            if (mRedactorModel.isEdited()) {
                mBinding.firstNumber.setValue(mRedactorModel.getCurrentRecipe().getCookingTime() / 60);
                mBinding.secondNumber.setValue(mRedactorModel.getCurrentRecipe().getCookingTime() % 60);
            }

            mBinding.secondNumber.setOnValueChangedListener(mRedactorModel.getPickerChangedListener(isTimePicker.get(), false));
        } else {
            if (mRedactorModel.isEdited()) {
                mBinding.firstNumber.setValue(mRedactorModel.getCurrentRecipe().getPortions());
            }
            initNumberPickerValues();
        }
        return mBinding.getRoot();
    }

    private void initNumberPickerValues() {
        mBinding.title.setText(getResources().getString(R.string.persons_number));
        mBinding.firstNumber.setMinValue(1);
        mBinding.firstNumber.setMaxValue(MAX_PORTIONS);
    }

    private void initTimePickerValues() {
        mBinding.title.setText(getResources().getString(R.string.choose_cooking_time));
        mBinding.firstNumber.setMinValue(0);
        mBinding.secondNumber.setMinValue(0);
        mBinding.firstNumber.setMaxValue(MAX_HOURS);
        mBinding.secondNumber.setMaxValue(MAX_MINUTES);
        mBinding.secondNumber.setMaxValue(MAX_MINUTES);
    }

    public void setRedactorModel(RedactorViewModel mRedactorModel) {
        this.mRedactorModel = mRedactorModel;
    }

}
