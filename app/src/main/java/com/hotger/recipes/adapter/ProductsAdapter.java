package com.hotger.recipes.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotger.recipes.R;
import com.hotger.recipes.databinding.ItemProductLineBinding;
import com.hotger.recipes.utils.model.Product;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.ControllableActivity;

import java.util.ArrayList;

/**
 * Adapter for handling entering products and its quanitity
 *
 * Адаптер для обработки ингридиентов внутри редактора рецептов
 */
public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    /**
     * Context variable
     */
    private ControllableActivity activity;

    private boolean isEditable;

    private boolean isDetailed;

    /**
     * All selected products
     */
    private ArrayList<Product> data; //TODO записывать сразу в текущий рецепт

    public ProductsAdapter(ControllableActivity context, ArrayList<Product> data, boolean isEditable,
                           boolean isDetailed) {
        this.activity = context;
        this.data = data;
        this.isEditable = isEditable;
        this.isDetailed = isDetailed;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        return new ViewHolder(ItemProductLineBinding.inflate(inflater, parent, false).getRoot(), isEditable);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product productLine = data.get(position);
        holder.binding.productName.setText(productLine.getIngredientById());
        Drawable drawable = getAmountDrawable(productLine.getMeasure(), activity);
        holder.binding.amountIcon.setText(activity.getResources().getStringArray(R.array.measures_array)[productLine.getMeasure()]);
        holder.binding.amountIcon.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        if (!isEditable) {
            holder.binding.finalAmount.setText(Utils.numberToString(productLine.getAmount()));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Returns drawable depending on {@param quantity} of the product
     *
     * @param quantity - quanitity of the product
     * @param context  - activity
     * @return drawable received from the resource
     */
    private Drawable getAmountDrawable(int quantity, Context context) {
        Drawable drawable;
        switch (quantity) {
            case Utils.Measure.LITERS:
            case Utils.Measure.CUPS:
                drawable = context.getResources().getDrawable(R.drawable.ic_glass_white);
                break;
            case Utils.Measure.TABLESPOON:
            case Utils.Measure.TEASPOON:
                drawable = context.getResources().getDrawable(R.drawable.ic_spoon);
                break;
            case Utils.Measure.PIECE:
                drawable = context.getResources().getDrawable(R.drawable.ic_pcs_white);
                break;
            case Utils.Measure.GRAMM:
            case Utils.Measure.KG:
            case Utils.Measure.OUNCE:
            default:
                drawable = context.getResources().getDrawable(R.drawable.ic_gram);
                break;
        }

        return drawable;
    }

    /**
     * Add a new data to variable
     *
     * @param line - new product
     */
    public void addData(Product line) {
        data.add(line);
        notifyItemInserted(data.size() - 1);
    }

    /**
     * Checks if product is already in set
     *
     * @param productName - product name to be checked
     * @return <code>true</code> if is in set
     */
    public boolean isAlreadyInSet(String productName) {
        for (Product line : data) {
            if (line.getIngredientId().equals(productName))
                return true;
        }

        return false;
    }

    //region Getters and Setters
    public ArrayList<Product> getData() {
        return data;
    }

    public void setData(ArrayList<Product> shots) {
        data = shots;
    }

    //endregion

    class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * Data binding variable
         */
        ItemProductLineBinding binding;

        ViewHolder(final View itemView, boolean isEditable) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            binding.setIsDetailed(isDetailed);
            if (isEditable) {
                View.OnClickListener listener = view -> {
                    double temp = Double.parseDouble(binding.finalAmount.getText().toString());
                    if (view.getId() == R.id.btn_sub) {
                        if (temp > 0) {
                            binding.finalAmount.setText(Utils.numberToString(temp - 1));
                        }
                    } else {
                        binding.finalAmount.setText(Utils.numberToString(temp + 1));
                    }
                };
                binding.btnAdd.setOnClickListener(listener);
                binding.btnSub.setOnClickListener(listener);
                binding.finalAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.toString().length() != 0) {
                            double val = Double.valueOf(editable.toString());
                            data.get(getAdapterPosition()).setAmount(val);
                        } else {
                            data.get(getAdapterPosition()).setAmount(0);
                        }
                    }
                });
                binding.amountIcon.setOnClickListener(view -> showDialog());
            } else {
                binding.btnSub.setVisibility(View.GONE);
                binding.btnAdd.setVisibility(View.GONE);
                binding.finalAmount.setEnabled(false);
            }
        }

        private void showDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppDialog));
            builder.setTitle(activity.getResources().getString(R.string.choose_measure));
            builder.setItems(activity.getResources().getStringArray(R.array.measures_array),
                    (dialog, which) -> changeMeasure(which));
            builder.show();
        }

        /**
         * Changes measure of the product
         *
         * @param measurePosition - position choosed from the dialog
         */
        private void changeMeasure(int measurePosition) {
            data.get(getAdapterPosition()).setMeasure(measurePosition);
            Drawable drawable = getAmountDrawable(measurePosition, activity);
            binding.amountIcon.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            binding.amountIcon.setText(activity.getResources().getStringArray(R.array.measures_array)[measurePosition]);
        }
    }
}
