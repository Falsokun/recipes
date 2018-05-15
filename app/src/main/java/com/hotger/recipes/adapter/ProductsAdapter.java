package com.hotger.recipes.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Rational;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.airbnb.lottie.LottieAnimationView;
import com.hotger.recipes.R;
import com.hotger.recipes.database.dao.ProductDao;
import com.hotger.recipes.databinding.ItemProductLineBinding;
import com.hotger.recipes.model.Ingredient;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.RecipeNF;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.MeasureUtils;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.ControllableActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.hotger.recipes.view.ShoppingListActivity.SHOPPING_LIST_CHECKED;
import static com.hotger.recipes.view.ShoppingListActivity.SHOPPING_LIST_ID;
import static com.hotger.recipes.view.ShoppingListActivity.SHOPPING_LIST_UNCHECKED;

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

    private Keyboard mKeyboard;

    private KeyboardView mKeyboardView;

    /**
     * All selected products
     */
    private List<Product> data; //TODO записывать сразу в текущий рецепт

    private double koeff = 1;

    private List<Product> unchecked;
    private List<Product> checked;

    public ProductsAdapter(ControllableActivity context, List<Product> data, boolean isEditable,
                           boolean isDetailed, boolean isShoppingList) {
        this.activity = context;
        this.data = data;
        this.isEditable = isEditable;
        this.isDetailed = isDetailed;
        this.isShoppingList = isShoppingList;
        if (isShoppingList) {
            ProductDao dao = AppDatabase.getDatabase(context)
                    .getProductDao();
            unchecked = dao.getProducts(SHOPPING_LIST_UNCHECKED);
            checked = dao.getProducts(SHOPPING_LIST_CHECKED);
            data.addAll(unchecked);
            data.addAll(checked);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        return new ViewHolder(ItemProductLineBinding.inflate(inflater, parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product productLine = data.get(position);
        holder.binding.productName.setText(productLine.getIngredientById(activity,
                productLine.getIngredientId()));
        Drawable drawable = MeasureUtils.getDrawableByMeasure(activity, productLine.getMeasure());
        holder.binding.amountIcon.setText(productLine.getMeasure());
        holder.binding.amountIcon
                .setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        holder.binding.finalAmount
                .setText(Product.doubleToStringWithKoeff(productLine.getRationalAmount(), koeff));
        if (holder.binding.getIsDetailed()
                && !holder.binding.getIsEditable()
                && productLine.getRationalAmount().getNumerator() == 0) {
            holder.binding.finalAmount.setVisibility(View.GONE);
        }

        holder.binding.lottieAnim.setOnClickListener(view -> {
            checkAnimStatus(holder.binding.lottieAnim, productLine);
            animateView(holder.binding.lottieAnim, true, 0f, 0.5f, null);
        });

        if (isShoppingList) {
            holder.binding.checkAnim.setAnimation(R.raw.check_animation);
            holder.binding.checkAnim.setVisibility(View.VISIBLE);
            if (checked.contains(productLine)) {
                holder.binding.checkAnim.setProgress(1f);
                holder.binding.checkAnim.setEnabled(false);
            }

            holder.binding.checkAnim.setOnClickListener(view -> {
                AnimatorListenerAdapter animEnd = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        data.remove(position);
                        changeItemList(productLine);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, data.size() - 1);
                        super.onAnimationEnd(animation);
                    }
                };

                animateView(holder.binding.checkAnim, false, 0f, 1f, animEnd);
            });
        }
    }

    private void changeItemList(Product product) {
        unchecked.remove(product);
        checked.add(product);
        data.add(product);
        saveItemToList(activity, SHOPPING_LIST_CHECKED, product);
        removeItemToList(activity, SHOPPING_LIST_UNCHECKED, product);
    }

    public void clearAllChecked() {
        List<Product> products = AppDatabase.getDatabase(activity)
                .getProductDao()
                .getProducts(SHOPPING_LIST_CHECKED);
        for (Product product : products) {
            removeItemFromList(activity, SHOPPING_LIST_ID, product);
        }

        AppDatabase.getDatabase(activity).getProductDao().removeWhereId(SHOPPING_LIST_CHECKED);
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

    private void removeItemToList(ControllableActivity activity, String shoppingListId, Product product) {
        AppDatabase.getDatabase(activity).getProductDao()
                .deleteWhereId(shoppingListId, product.getIngredientId());
    }


    //TODO: Эта функция удаляет весь продукт, надо вставить ID
    public void removeItemFromList(ControllableActivity activity, String shoppingListId, Product product) {
        AppDatabase.getDatabase(activity).getProductDao().delete(product);
    }

    /**
     * Animates {@param animationView} with its animation from {@param start} to {@param end}
     *
     * @param animationView   - view to be animated
     * @param shouldCalculate - boolean value which helps to figure out is it IN animation or OUT
     * @param start           - start value
     * @param end             - end value
     */
    private void animateView(LottieAnimationView animationView, boolean shouldCalculate, float start, float end, AnimatorListenerAdapter onEnd) {
        float anim_start;
        float anim_end;
        if (shouldCalculate) {
            anim_start = animationView.getProgress() == 0f || animationView.getProgress() == 1f ? start : end;
            anim_end = anim_start == 0f ? end : 1f;
        } else {
            anim_start = start;
            anim_end = end;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(anim_start, anim_end).setDuration(500);
        animator.addUpdateListener(valueAnimator -> animationView.setProgress((Float) valueAnimator.getAnimatedValue()));
        animator.addListener(onEnd);
        animator.start();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Checks if product is already in set
     *
     * @param productName - product name to be unchecked
     * @return <code>true</code> if is in set
     */
    public boolean isAlreadyInSet(String productName) {
        for (Product line : data) {
            if (line.getIngredientId().equals(productName))
                return true;
        }

        return false;
    }

    public boolean isShoppingList() {
        return isShoppingList;
    }

    public void setShoppingList(boolean shoppingList) {
        isShoppingList = shoppingList;
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

    public KeyboardView.OnKeyboardActionListener getOnKeyboardActionListener() {
        return new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int primaryCode) {

            }

            @Override
            public void onRelease(int primaryCode) {

            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {

            }

            @Override
            public void onText(CharSequence text) {

            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        };
    }
    //endregion

    public Keyboard initKeyboard(KeyboardView keyboardView) {
        mKeyboard = new Keyboard(activity, R.xml.keyboard);
        mKeyboardView = keyboardView;
        return mKeyboard;
    }

    public void addUnchecked(Product product) {
        unchecked.add(0, product);
        data.add(0, product);
        saveItemToList(activity, SHOPPING_LIST_UNCHECKED, product);
        notifyDataSetChanged();
    }

    public void removeCheckedItems() {
        int exSize = data.size() - 1;
        data.removeAll(checked);
        checked.clear();
        AppDatabase.getDatabase(activity).getProductDao().removeWhereId(SHOPPING_LIST_CHECKED);
        notifyItemRangeRemoved(unchecked.size(), exSize);
    }

    public void sortProducts() {
        if (data.size() > 0) {
            Collections.sort(data, (o1, o2) -> Rational.parseRational(o2.getAmount())
                    .compareTo(Rational.parseRational(o1.getAmount())));
        }
    }

    public void add(Product product) {
        data.add(product);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        /**
         * Data binding variable
         */
        ItemProductLineBinding binding;

        //TODO: стринговое представление 1 1/2 во вьюхе
        ViewHolder(final View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
            binding.setIsDetailed(isDetailed);
            binding.setIsEditable(isEditable);
            itemView.setOnLongClickListener(this);

            if (isEditable) {
                if (mKeyboardView != null) {
                    mKeyboardView.setOnKeyboardActionListener(getOnKeyboardActionListener());
                    binding.finalAmount.setOnClickListener(v -> openKeyboard(v));
                }

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
                if (isDetailed) {
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
                                //TODO: ТУТ ИСПРАВИТЬ
                                data.get(getAdapterPosition()).setRationalAmount(new Rational((int) val, 1));
                            } else {
                                data.get(getAdapterPosition()).setRationalAmount(new Rational(0, 1));
                            }
                        }
                    });
                }

                binding.amountIcon.setOnClickListener(view -> showDialog());
            } else {
                binding.btnSub.setVisibility(View.GONE);
                binding.btnAdd.setVisibility(View.GONE);
                binding.finalAmount.setEnabled(false);
            }
        }

        public void openKeyboard(View v) {
            mKeyboardView.setVisibility(View.VISIBLE);
            mKeyboardView.setEnabled(true);
            if (v != null)
                ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        /**
         * Show dialog with options about saving measure
         */
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
            Drawable drawable = MeasureUtils.getDrawableByMeasure(activity, MeasureUtils.getFromArray(position));
            binding.amountIcon.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            binding.amountIcon.setText(activity.getResources().getStringArray(R.array.measures_array)[position]);
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_Material_Dialog_Alert);
            List<Ingredient> list = AppDatabase.getDatabase(v.getContext())
                    .getIngredientDao()
                    .getEnTranslation(binding.productName.getText().toString());
            builder.setTitle(activity.getString(R.string.translation))
                    .setMessage(list.get(0).getEn())
                    .setNeutralButton(activity.getString(R.string.OK), null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }
    }
}
