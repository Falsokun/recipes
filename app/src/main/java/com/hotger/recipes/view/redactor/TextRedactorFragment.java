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
import android.widget.TextView;

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

    /**
     * Current number in the numbered list
     */
    private int marker = 1;

    /**
     * Variable if last input change was deleting
     */
    private boolean keyDelete = false;

    private boolean isEditedRecipe = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_redactor_input_text, container, false);
        numberedText = rootView.findViewById(R.id.editable_text);
        TextView title = rootView.findViewById(R.id.title);
        title.setText(R.string.write_recipe);
        if (isEditedRecipe) {
            numberedText.setText(mRedactorModel.getCurrentRecipe().getPreparations());
        } else {
            addMarker();
        }
        numberedText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int count) {
                keyDelete = i1 - count == 1;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!keyDelete) {
                    addMarker();
                } else {
                    deleteMarker();
                    if (editable.toString().length() == 2) {
                        editable.append(" ");
                    }

                }

                if (editable.toString().length() > 3) {
                    mRedactorModel.currentRecipe.setPreparations(editable.toString());
                }
            }
        });
        return rootView;
    }

    /**
     * Deleting "#." from the new string if is necessary
     */
    private void deleteMarker() {
        String wholeText = numberedText.getText().toString();
        int cursor = numberedText.getSelectionEnd();
        if (wholeText.length() > 3) {
            String last = wholeText.substring(wholeText.length() - 3);
            if (wholeText.substring(wholeText.length() - 3, wholeText.length() - 2).equals("\n")) {
                numberedText.setText(wholeText.replace(last, ""));
                numberedText.setSelection(cursor - 3);
                marker--;
            }
        }
    }

    /**
     * Adding marker "#." for the new string if necessary
     */
    private void addMarker() {
        String wholeText = numberedText.getText().toString();
        if (wholeText.length() == 0 || wholeText.substring(wholeText.length() - 1, wholeText.length()).equals("\n")) {
            numberedText.getText().append(marker + ". ");
            marker++;
        }
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
