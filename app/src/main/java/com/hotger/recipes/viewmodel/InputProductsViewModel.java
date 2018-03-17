package com.hotger.recipes.viewmodel;

import android.app.AlertDialog;
import android.databinding.Bindable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hotger.recipes.BR;
import com.hotger.recipes.R;
import com.hotger.recipes.adapter.DataHintAdapter;
import com.hotger.recipes.adapter.ProductsAdapter;
import com.hotger.recipes.utils.model.Product;
import com.hotger.recipes.view.ControllableActivity;

import java.util.ArrayList;

public class InputProductsViewModel extends MViewModel {

    private ProductsAdapter productsAdapter;
    private DataHintAdapter dataHintAdapter;
    private ArrayList<Product> products;
    private ControllableActivity activity;
    private String productName;

    public InputProductsViewModel(ControllableActivity activity, ArrayList<Product> products, boolean isEditable, boolean isDetailed) {
        this.products = products;
        this.activity = activity;
        productsAdapter = new ProductsAdapter(activity, products, isEditable, isDetailed);
        dataHintAdapter = new DataHintAdapter(activity, R.layout.item_list, activity.getDatabase(), "en");
    }

    @Override
    public void OnResume() {

    }

    @Override
    public void OnPause() {

    }

    //region actions
    public void onHintItemClickAction(View view) {
        TextView childView = view.findViewById(R.id.product_name);
        String productName = childView.getText().toString();
        if (!productsAdapter.isAlreadyInSet(productName)) {
            products.add(new Product(productName));
        } else {
            Toast.makeText(activity, "Already in list", Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    //region Listeners
    public TextView.OnEditorActionListener getOnEditorActionListener(int count) {
        return (TextView textView, int actionId, KeyEvent keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                int listNum = count;
                if (listNum == 0) {
                    showAlertDialog();
                }
            }
            return false;
        };
    }

    public AdapterView.OnItemClickListener getOnHintItemClickListener(EditText inputView) {
        return (adapterView, view, i, l) -> {
            onHintItemClickAction(view);
//            Utils.hideKeyboard(view);
            inputView.setText("");
        };
    }

    public TextWatcher getProductTextChangeListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataHintAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(activity.getString(R.string.no_such_product))
                .setMessage(activity.getString(R.string.should_add))
                .setCancelable(false)
                .setNegativeButton(activity.getString(R.string.no),
                        (dialog, id) -> dialog.cancel())
                .setPositiveButton(activity.getString(R.string.yes),
                        (dialog, id) -> {
                            dialog.cancel();
                            addProductToBase();
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    //endregion

    //region Getters and Setters
    private void addProductToBase() {
        Toast.makeText(activity, "add product to base", Toast.LENGTH_SHORT).show();
    }

    public DataHintAdapter getDataHintAdapter() {
        return dataHintAdapter;
    }

    public ProductsAdapter getProductsAdapter() {
        return productsAdapter;
    }

    @Bindable
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
        notifyPropertyChanged(BR.productName);
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public ItemTouchHelper getItemTouchListener() {
        ItemTouchHelper.SimpleCallback callback =  new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                productsAdapter.getData().remove(viewHolder.getAdapterPosition());
                productsAdapter.notifyDataSetChanged();
            }
        };

        return new ItemTouchHelper(callback);
    }

    //endregion
}
