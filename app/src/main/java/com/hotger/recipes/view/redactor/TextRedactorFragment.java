package com.hotger.recipes.view.redactor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hotger.recipes.R;
import com.hotger.recipes.viewmodel.RedactorViewModel;

public class TextRedactorFragment extends Fragment {

    /**
     * Redactor view model
     */
    private RedactorViewModel mRedactorModel;

    /**
     * Numbered list for text
     */
    private EditText numberedText;

    private boolean isEditedRecipe = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_redactor_input_text, container, false);
        numberedText = rootView.findViewById(R.id.editable_text);
        numberedText.setEnabled(isEditedRecipe);
        if (mRedactorModel.getCurrentRecipe() != null) {
            numberedText.setText(mRedactorModel.getCurrentRecipe().getPreparations());
        }

        numberedText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mRedactorModel.getCurrentRecipe().setPreparations(editable.toString());
            }
        });
        return rootView;
    }

    /**
     * Setting model for the fragment
     *
     * @param mRedactorModel - model
     */
    public void setRedactorModel(RedactorViewModel mRedactorModel) {
        this.mRedactorModel = mRedactorModel;
        isEditedRecipe = mRedactorModel.isEdited();
    }
}
