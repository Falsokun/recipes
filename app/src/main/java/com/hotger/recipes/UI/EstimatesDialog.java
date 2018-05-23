package com.hotger.recipes.UI;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.DialogAdapter;
import com.hotger.recipes.model.GsonModel.NutritionEstimates;

import java.util.List;

/**
 * Custom dialog with recyclerView inside
 */
public class EstimatesDialog extends Dialog implements View.OnClickListener {

    private DialogAdapter adapter;

    public EstimatesDialog(@NonNull Context context, List<NutritionEstimates> data) {
        super(context);
        adapter = new DialogAdapter(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_estimates);
        Button okButton = findViewById(R.id.ok_btn);
        okButton.setOnClickListener(this);
        RecyclerView recyclerView;
        if (adapter.getItemCount() != 0) {
            recyclerView = findViewById(R.id.content_rv);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            ((TextView)findViewById(R.id.title)).setText(R.string.no_data_available);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
