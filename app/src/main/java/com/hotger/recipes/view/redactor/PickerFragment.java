package com.hotger.recipes.view.redactor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentRedactorPickersBinding;
import com.hotger.recipes.viewmodel.RedactorViewModel;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class PickerFragment extends Fragment implements com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {
    /**
     * Redactor view model
     */
    private RedactorViewModel mRedactorModel;

    private FragmentRedactorPickersBinding mBinding;

    private final String PREP_TAG = "prep_tag";
    private final String COOKING_TAG = "cooking_tag";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_redactor_pickers, container, false);
        int min = mRedactorModel.getCurrentRecipe().getPrepTimeMinutes();
        mBinding.prepTime.setText(timeFormatter(min / 60, min % 60));
        min = mRedactorModel.getCurrentRecipe().getCookingTimeInMinutes();
        mBinding.cookingTime.setText(timeFormatter(min / 60, min % 60));
        mBinding.prepTime.setOnClickListener(v -> openTimePicker(mRedactorModel.getCurrentRecipe().getPrepTimeMinutes(), PREP_TAG));
        mBinding.cookingTime.setOnClickListener(v -> openTimePicker(mRedactorModel.getCurrentRecipe().getCookingTimeInMinutes(), COOKING_TAG));
        mBinding.photoSrcBtn.setOnClickListener(v -> chooseImage());
        return mBinding.getRoot();
    }

    public void setRedactorModel(RedactorViewModel mRedactorModel) {
        this.mRedactorModel = mRedactorModel;
    }

    public void openTimePicker(int initTime, String tag) {
        TimePickerDialog dialog = TimePickerDialog.newInstance(this,
                 initTime / 60, initTime % 60, 0,
                true);
        dialog.show(getActivity().getFragmentManager(), tag);
    }

    //????
    @SuppressLint("ResourceType")
    private void chooseImage() {
        ArrayList<Image> images = new ArrayList<>();
        ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
                .setToolbarColor(getResources().getString(R.color.colorPrimaryDark))         //  Toolbar color
//                .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
                .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
//                .setProgressBarColor("#4CAF50")     //  ProgressBar color
                .setBackgroundColor("#212121")      //  Background color
                .setCameraOnly(false)               //  Camera mode
                .setMultipleMode(true)              //  Select multiple images or single image
                .setFolderMode(true)                //  Folder mode
                .setShowCamera(true)                //  Show camera button
                .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
                .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
                .setDoneTitle(getString(R.string.OK))               //  Done button title
                .setMaxSize(1)                     //  Max images can be selected
//                .setSavePath("ImagePicker")         //  Image capture folder name
                .setSelectedImages(images)          //  Selected images
                .setKeepScreenOn(true)              //  Keep screen on when selecting images
                .start();                           //  Start ImagePicker
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            String path = images.get(0).getPath();
            mRedactorModel.getCurrentRecipe().setImageURL(path);
        }
        super.onActivityResult(requestCode, resultCode, data);  // THIS METHOD SHOULD BE HERE so that ImagePicker works with fragment
    }

    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (view.getTag().equals(PREP_TAG)) {
            mRedactorModel.getCurrentRecipe().setPrepTimeInMinutes(hourOfDay * 60 + minute);
            mBinding.prepTime.setText(timeFormatter(hourOfDay, minute));
        } else {
            mRedactorModel.getCurrentRecipe().setCookTimeInMinutes(hourOfDay * 60 + minute);
            mBinding.cookingTime.setText(timeFormatter(hourOfDay, minute));
        }
    }

    public String timeFormatter(int hour, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }
}
