package com.hotger.recipes.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hotger.recipes.R;
import com.hotger.recipes.model.NutritionEstimates;

import java.util.List;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {

    private List<NutritionEstimates> data;

    public DialogAdapter(List<NutritionEstimates> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DialogAdapter.ViewHolder(inflater.inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NutritionEstimates estimate = data.get(position);
        holder.text.setText(estimate.getDescription());
        holder.amount.setText(estimate.getValue());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView amount;

        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.name);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}
