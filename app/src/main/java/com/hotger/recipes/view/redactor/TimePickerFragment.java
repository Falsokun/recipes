package com.hotger.recipes.view.redactor;

import android.support.v4.app.Fragment;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentRedactorCookingTimeBinding;
import com.hotger.recipes.viewmodel.RedactorViewModel;

public class TimePickerFragment extends Fragment {
    /**
     * Redactor view model
     */
    private RedactorViewModel mRedactorModel;

    private FragmentRedactorCookingTimeBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_redactor_cooking_time, container, false);
        hideAmPmLayout(mBinding.timePicker);
        mBinding.timePicker.setCurrentHour(0);
        mBinding.timePicker.setCurrentMinute(0);
        mBinding.timePicker.setIs24HourView(true);
        mBinding.timePicker.setOnTimeChangedListener((view, hourOfDay, minute) ->
                mRedactorModel.getCurrentRecipe().setCookTimeInMinutes(hourOfDay * minute));
        return mBinding.getRoot();
    }

    public void setRedactorModel(RedactorViewModel mRedactorModel) {
        this.mRedactorModel = mRedactorModel;
    }

    private void hideAmPmLayout(TimePicker picker) {
        final int id = Resources.getSystem().getIdentifier("ampm_layout", "id", "android");
        final View amPmLayout = picker.findViewById(id);
        if(amPmLayout != null) {
            amPmLayout.setVisibility(View.GONE);
        }
    }
}
