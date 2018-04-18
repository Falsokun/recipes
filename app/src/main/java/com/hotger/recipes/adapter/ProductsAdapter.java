package com.hotger.recipes.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.hotger.recipes.R;
import com.hotger.recipes.databinding.ItemProductLineBinding;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.RecipeNF;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.ControllableActivity;
import com.hotger.recipes.viewmodel.InputProductsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hotger.recipes.view.ShoppingListActivity.SHOPPING_LIST_ID;

/**
 * Adapter for handling entering products and its quanitity
 * <p>
 * Адаптер для обработки ингридиентов внутри редактора рецептов
 */
public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    /**
     * Context variable
     */
    private ControllableActivity activity;

    private boolean isEditable;

    private boolean isDetailed;

    private boolean isShoppingList;

    /**
     * All selected products
     */
    private List<Product> data; //TODO записывать сразу в текущий рецепт

    private double koeff = 1;

    public ProductsAdapter(ControllableActivity context, List<Product> data, boolean isEditable,
                           boolean isDetailed, boolean isShoppingList) {
        this.activity = context;
        this.data = data;
        this.isEditable = isEditable;
        this.isDetailed = isDetailed;
        this.isShoppingList = isShoppingList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        return new ViewHolder(ItemProductLineBinding.inflate(inflater, parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product productLine = data.get(position);
        holder.binding.productName.setText(productLine.getIngredientById(activity));
        Drawable drawable = getAmountDrawable(productLine.getDrawableByMeasure(), activity);
        holder.binding.amountIcon.setText(productLine.getMeasure());
        holder.binding.amountIcon.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        holder.binding.finalAmount.setText(Utils.numberToString(productLine.getAmount()* koeff));
        if (holder.binding.getIsDetailed() && productLine.getAmount() == 0) {
            holder.binding.setIsDetailed(false);
        }

        holder.binding.lottieAnim.setOnClickListener(view -> {
            checkAnimStatus(holder.binding.lottieAnim, productLine);
            animateView(holder.binding.lottieAnim, true, 0f, 0.5f);
        });

        if (isShoppingList) {
            holder.binding.checkAnim.setAnimation(R.raw.check_animation);
            holder.binding.checkAnim.setVisibility(View.VISIBLE);
            holder.binding.checkAnim.setOnClickListener(view -> {
                animateView(holder.binding.checkAnim, false, 0f, 1f);
                holder.binding.checkAnim.setEnabled(false);
                Collections.swap(data, position, data.size() - 1);
                notifyItemMoved(position, data.size() - 1);
            });
        }
    }

    private void checkAnimStatus(LottieAnimationView animationView, Product product) {
        if (animationView.getProgress() == 0.5f) {
            removeItemFromList(activity, SHOPPING_LIST_ID, product);
        } else {
            saveItemToList(activity, SHOPPING_LIST_ID, product);
        }
    }

    public void saveItemToList(ControllableActivity activity, String shoppingListId, Product product) {
        if (AppDatabase.getDatabase(activity).getRecipeDao().getRecipesById(shoppingListId).size() == 0) {
            RecipeNF recipeNF = new RecipeNF();
            recipeNF.setId(shoppingListId);
            AppDatabase.getDatabase(activity).getRecipeDao().insert(recipeNF);
        }

        product.setRecipeId(shoppingListId);
        AppDatabase.getDatabase(activity).getProductDao().insert(product);
    }

    public void removeItemFromList(ControllableActivity activity, String shoppingListId, Product product) {
        AppDatabase.getDatabase(activity).getProductDao().delete(product);
    }

    private void animateView(LottieAnimationView animationView, boolean shouldCalculate, float start, float end) {
        float anim_start;
        float anim_end;
        if (shouldCalculate) {
            anim_start = animationView.getProgress() == 0f || animationView.getProgress() == 1f ? start : end;
            anim_end = anim_start == 0f ? end : 1f;
        } else {
            anim_start = start;
            anim_end = end;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(anim_start, anim_end).setDuration(1000);
        animator.addUpdateListener(valueAnimator -> animationView.setProgress((Float) valueAnimator.getAnimatedValue()));
        animator.start();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //TODO все очень плохо

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
    public List<Product> getData() {
        return data;
    }

    public void setData(List<Product> shots) {
        data = shots;
        notifyDataSetChanged();
    }

    public void setKoeff(double koeff) {
        this.koeff = koeff;
        notifyDataSetChanged();
    }

    //endregion

    class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * Data binding variable
         */
        ItemProductLineBinding binding;

        ViewHolder(final View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            binding.setIsDetailed(isDetailed);
            binding.setIsEditable(isEditable);

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
            builder.setItems(activity.getResources().getStringArray(R.array.measures_array), (dialog, which) -> {
                String[] arr = activity.getResources().getStringArray(R.array.measures_array);
                changeMeasure(arr[which], which);
            });

            builder.show();
        }

        /**
         * Changes measure of the product
         *
         * @param measurePosition - position choosed from the dialog
         */
        private void changeMeasure(String measurePosition, int position) {
            data.get(getAdapterPosition()).setMeasure(measurePosition);
            Drawable drawable = getAmountDrawable(position, activity);
            binding.amountIcon.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            binding.amountIcon.setText(activity.getResources().getStringArray(R.array.measures_array)[position]);
        }
    }
}
