package com.hotger.recipes.viewmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.Bindable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
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
import com.hotger.recipes.adapter.DataListAdapter;
import com.hotger.recipes.adapter.ProductsAdapter;
import com.hotger.recipes.database.FirebaseUtils;
import com.hotger.recipes.model.ApiRecipe;
import com.hotger.recipes.model.Ingredient;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.ControllableActivity;

import java.util.List;

public class InputProductsViewModel extends ViewModel {

    private ProductsAdapter productsAdapter;
    private DataListAdapter dataHintAdapter;
    private List<Product> products;
    private ControllableActivity activity;
    private String productName;

    public InputProductsViewModel(ControllableActivity activity, List<Product> products, boolean isEditable, boolean isDetailed, boolean isShoppingList) {
        this.products = products;
        this.activity = activity;
        productsAdapter = new ProductsAdapter(activity, products, isEditable, isDetailed, isShoppingList);
        List<String> data = AppDatabase.getDatabase(activity).getIngredientDao().getEnglishNames();
        data.addAll(AppDatabase.getDatabase(activity).getIngredientDao().getRussianNames());
        dataHintAdapter = new DataListAdapter(activity, R.layout.item_list, data);
    }

    @Override
    public void OnResume() {

    }

    @Override
    public void OnPause() {

    }

    //region actions
    public void onHintItemClickAction(View view) {
        TextView childView = view.findViewById(R.id.name);
        String productName = childView.getText().toString();
        if (!productsAdapter.isAlreadyInSet(productName)) {
            if (!productsAdapter.isDetailed()) { //for fridge and shopping
                boolean shouldAdd = true;
                String spId =  Utils.SP_RECIPES_ID.TYPE_SL_UNCHECKED;
                if (!productsAdapter.isShoppingList()) {
                    shouldAdd = false;
                    spId = Utils.SP_RECIPES_ID.TYPE_FRIDGE_ID;
                }

                productsAdapter.addAndSaveToDB(new Product(productName, activity), shouldAdd, spId);
            } else {
                productsAdapter.add(new Product(productName, activity));
            }
        } else {
            Toast.makeText(activity, "Already in list", Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    //region Listeners
    public TextView.OnEditorActionListener getOnEditorActionListener() {
        return (TextView textView, int actionId, KeyEvent keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (dataHintAdapter.isEmptyData().get()) {
                    showAlertDialog();
                }
            }
            return false;
        };
    }

    public AdapterView.OnItemClickListener getOnHintItemClickListener(EditText inputView) {
        return (adapterView, view, i, l) -> {
            onHintItemClickAction(view);
            Utils.hideKeyboard(activity);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog_Alert);
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
        AlertDialog.Builder adb = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog_Alert);
        adb.setTitle(activity.getString(R.string.enter_ingredient_info));
        View v = activity.getLayoutInflater().inflate(R.layout.dialog_ingredient, null);
        adb.setView(v);
        adb.setPositiveButton(R.string.OK, (dialog, which) -> {
            String enTitle = ((EditText) v.findViewById(R.id.en_title)).getText().toString();
            enTitle = enTitle.substring(0, 1).toUpperCase() + enTitle.substring(1).toLowerCase();
            String ruTitle = ((EditText) v.findViewById(R.id.ru_title)).getText().toString();
            ruTitle = ruTitle.substring(0, 1).toUpperCase() + ruTitle.substring(1).toLowerCase();
            FirebaseUtils.addIngredientToDb(enTitle, ruTitle);
            AppDatabase.getDatabase(activity).getIngredientDao().insert(new Ingredient(enTitle, ruTitle));
            dataHintAdapter.addIngredient(new Ingredient(enTitle, ruTitle));
        });

        AlertDialog ad = adb.create();
        ad.setCanceledOnTouchOutside(true);
        ad.show();
    }

    public DataListAdapter getDataHintAdapter() {
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

    public List<Product> getProducts() {
        return products;
    }

    public ItemTouchHelper getItemTouchListener(String recipeId) {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Product product = productsAdapter.getData().remove(viewHolder.getAdapterPosition());
                productsAdapter.removeItemFromList(activity, recipeId, product);
                productsAdapter.notifyDataSetChanged();
            }
        };

        return new ItemTouchHelper(callback);
    }
    //endregion

    public void saveListData(Context context, String listId) {
        AppDatabase.getDatabase(context).getProductDao().removeWhereId(listId);
        if (getProducts() == null || getProducts().size() == 0) {
            return;
        }

        List<Product> products = getProducts();
        for (Product product : products) {
            product.setRecipeId(listId);
        }

        ApiRecipe apiRecipe = new ApiRecipe();
        apiRecipe.setId(listId);
        AppDatabase.getDatabase(context).getRecipeDao().insert(apiRecipe);
        AppDatabase.getDatabase(context).getProductDao().insertAll(products);
    }

    public void restoreListData(Context context, String listId) {
        List<Product> list = AppDatabase.getDatabase(context)
                .getProductDao()
                .getProducts(listId);
        if (getProducts().size() == 0) {
            getProducts()
                    .addAll(list);
        }
    }

    public Keyboard getKeyboard(KeyboardView kview) {
        return productsAdapter.initKeyboard(kview);
    }
}
