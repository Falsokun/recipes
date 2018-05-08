package com.hotger.recipes.model;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hotger.recipes.R;
import com.hotger.recipes.adapter.DialogAdapter;

import java.util.List;

public class EstimatesDialog extends Dialog implements View.OnClickListener {

    private Button okButton;
    private RecyclerView recyclerView;
    private DialogAdapter adapter;

    public EstimatesDialog(@NonNull Context context, List<NutritionEstimates> data) {
        super(context);
        adapter = new DialogAdapter(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.estimates_dialog);
        okButton = findViewById(R.id.ok_btn);
        okButton.setOnClickListener(this);
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
