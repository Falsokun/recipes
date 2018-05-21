package com.hotger.recipes.view.redactor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.FragmentRedactorPickersBinding;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.viewmodel.RedactorViewModel;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Redactor Fragment
 */
public class PickerRedactorFragment extends Fragment implements com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {
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
        Recipe recipe = mRedactorModel.getCurrentRecipe();
        int cal = recipe.getCalories();
        mBinding.calSelector.setText(String.valueOf(cal));
        int min = recipe.getPrepTimeMinutes();
        mBinding.prepTime.setText(timeFormatter(min / 60, min % 60));
        min = recipe.getCookingTimeInMinutes();
        mBinding.portionsSelector.setText(recipe.getStringPortions());
        mBinding.cookingTime.setText(timeFormatter(min / 60, min % 60));
        mBinding.prepContainer.setOnClickListener(v ->
                openTimePicker(recipe.getPrepTimeMinutes(), PREP_TAG));
        mBinding.cookingTimeContainer.setOnClickListener(v ->
                openTimePicker(recipe.getCookingTimeInMinutes(), COOKING_TAG));
        mBinding.imageSrcContainer.setOnClickListener(v -> chooseImage());
        mBinding.caloriesContainer.setOnClickListener(v -> openNumberPickerDialog(getContext(),
                mBinding.calSelector, getString(R.string.number_of_calories), 20, 1000, 5, true));
        mBinding.portionsContainer.setOnClickListener(v -> openNumberPickerDialog(getContext(),
                mBinding.portionsSelector, getString(R.string.persons_number), 1, 20, 1, false));
        return mBinding.getRoot();
    }

    public void setRedactorModel(RedactorViewModel mRedactorModel) {
        this.mRedactorModel = mRedactorModel;
    }

    /**
     * Open time picker dialog
     *
     * @param initTime - init time
     * @param tag      - tag
     */
    public void openTimePicker(int initTime, String tag) {
        TimePickerDialog dialog = TimePickerDialog.newInstance(this,
                initTime / 60, initTime % 60, 0,
                true);
        dialog.show(getActivity().getFragmentManager(), tag);
    }

    /**
     * Opens fragment for choosing image
     */
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
                .setSelectedImages(images)          //  Selected images
                .setKeepScreenOn(true)              //  Keep screen on when selecting images
                .start();                           //  Start ImagePicker
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            String path = images.get(0).getPath();
            mBinding.photoSrcBtn.setText(path.split("/")[path.split("/").length - 1]);
            mRedactorModel.getCurrentRecipe().setImageURL(path);
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    /**
     * Format time in XX:XX format
     *
     * @param hour
     * @param minute
     * @return string time in convenient format
     */
    public String timeFormatter(int hour, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    public void openNumberPickerDialog(Context context, TextView view, String title,
                                       int min, int max, int step, boolean isCalories) {
        final Dialog d = new Dialog(context);
        d.setTitle(title);
        d.setContentView(R.layout.fragment_number_picker);
        Button okBtn = d.findViewById(R.id.ok_btn);
        final NumberPicker np = d.findViewById(R.id.number_picker);
        np.setOnLongPressUpdateInterval(50);
        np.setMaxValue(max);
        np.setMinValue(min);
        String[] array = new String[(max - min) / step];
        for (int i = 0; i < (max - min) / step; i++) {
            array[i] = Integer.toString(min + i * step);
        }

        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(array);

        okBtn.setOnClickListener(v -> {
            int res = np.getValue();
            if (isCalories) {
                res = (res - min) * step + min;
            }

            view.setText(String.valueOf(res));
            d.dismiss();
        });

        d.show();
    }
}
