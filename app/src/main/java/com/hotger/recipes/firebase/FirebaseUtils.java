package com.hotger.recipes.firebase;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hotger.recipes.App;
import com.hotger.recipes.Fake;
import com.hotger.recipes.IngredientFake;
import com.hotger.recipes.adapter.CardAdapter;
import com.hotger.recipes.database.RelationCategoryRecipe;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.model.RecipeNF;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.model.Ingredient;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.utils.AsyncCalls;
import com.hotger.recipes.utils.TranslateAPI;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.ControllableActivity;
import com.hotger.recipes.view.RecipeFragment;
import com.hotger.recipes.view.TranslateResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Firebase realtime DB
 */
//TODO: пора подключать liveData видимо
public class FirebaseUtils {

    public static final String CATEGORY = "categories";
    public static final String NO_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/falsorecipes.appspot.com/o/storage%2Fno_img.png?alt=media&token=ec61e886-86f7-43d8-a4f4-a609477dd509";
    public static final String RECIPES_REF = "recipes_ref";
    public static final String PRODUCTS_REF = "products_ref";
    public static final String CATEGORIES_REF = "categories_ref";
    private static final String PREVIEW_REF = "preview_ref";
    public static final String CATEGORY_REF_SEND = "category_ref_send";
    public static final String RECIPE_REF_EXTRA = "recipe_ref_extra";

    public static void saveIngredientsToDatabase(AppDatabase db) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("ingredients");

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //надо попробовать
//                GenericTypeIndicator<HashMap<String, Object>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, Object>>() {};
//                Map<String, Object> objectHashMap = dataSnapShot.getValue(objectsGTypeInd);
//                ArrayList<Object> objectArrayList = new ArrayList<Object>(objectHashMap.values());
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                    Ingredient in = jobSnapshot.getValue(Ingredient.class);
                    ingredients.add(in);
                }

                db.getIngredientDao().insertAll(ingredients);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public static void saveCategoryToDatabase(Context context, AppDatabase db, String reference) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(reference);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Category>> t = new GenericTypeIndicator<ArrayList<Category>>() {
                };
                ArrayList<Category> categories = dataSnapshot.getValue(t);
                db.getCategoryDao().insertAll(categories);
                saveToDatabase(context);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private static void saveToDatabase(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        for (Category category : db.getCategoryDao().getAllCategories()) {
            AsyncCalls.saveCategoryToDB(context, category.getSearchValue(), false);
        }
    }

    //---------------------------------------

    /**
     * Для начальной инициализации с уникальными ключами
     *
     * @param db
     */
    public static void initDatabase(AppDatabase db) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("ingredients");

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                GenericTypeIndicator<ArrayList<Fake>> t = new GenericTypeIndicator<ArrayList<Fake>>() {
                };
                ArrayList<Fake> ingredients = dataSnapshot.getValue(t);
                for (Fake fake : ingredients) {
                    addNewToDatabase(fake.getEn(), fake.getRu(), fake.getMeasure(), database);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public static void addNewToDatabase(String en, String ru, String measure, FirebaseDatabase database) {
        DatabaseReference ref = database.getReference("ingredients").push();
        ref.setValue(new Ingredient(en, ru, measure));
    }

    public static void saveRecipeToFirebase(Recipe recipe, List<RelationCategoryRecipe> relations,
                                            RecipePrev prev) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference().child(RECIPES_REF).push().setValue(recipe.getRecipe());
        DatabaseReference productsRef = db.getReference().child(PRODUCTS_REF);
        DatabaseReference categoryRef = db.getReference().child(CATEGORIES_REF);
        db.getReference().child(PREVIEW_REF).push().setValue(prev);
        for (Product p : recipe.getProducts()) {
            productsRef.push().setValue(p);
        }

        for (RelationCategoryRecipe rel : relations) {
            categoryRef.push().setValue(rel);
        }
    }

    public static Recipe getRecipeFromFirebase(String id, ControllableActivity activity) {
        RecipeFragment fragment = new RecipeFragment();
        activity.setCurrentFragment(fragment, true, fragment.getTag());

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Query query = db.child(RECIPES_REF).orderByChild("id").equalTo(id).limitToFirst(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RecipeNF recipenf = null;
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    recipenf = shot.getValue(RecipeNF.class);
                }

                Recipe recipe = new Recipe(recipenf);

                db.child(CATEGORIES_REF).orderByChild("recipeId").equalTo(id).addValueEventListener(getValueListener(new RelationCategoryRecipe(), activity));
                db.child(PRODUCTS_REF).orderByChild("recipeId").equalTo(id).addValueEventListener(getValueListener(new Product(), activity));

                Intent intent = new Intent(Utils.RECIPE_OBJ);
                intent.putExtra(Utils.RECIPE_OBJ, recipe);
                intent.putExtra(RECIPES_REF, true);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }

    public static <T> ValueEventListener getValueListener(T cl, ControllableActivity activity) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<T> values = new ArrayList<>();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    values.add((T) shot.getValue(cl.getClass()));
                }

                Intent intent = new Intent(RECIPES_REF);
                intent.putExtra(RECIPES_REF, true);
                if (cl.getClass().equals(RelationCategoryRecipe.class)) {
                    intent.putExtra(CATEGORY_REF_SEND, true);
                } else {
                    intent.putExtra(CATEGORY_REF_SEND, false);
                }

                intent.putExtra(RECIPE_REF_EXTRA, values);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public static void getAllRecipesInCategory(String typeMyRecipes, CardAdapter adapter) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(CATEGORIES_REF).orderByChild("categoryId")
                .startAt(typeMyRecipes)
                .endAt(typeMyRecipes + "\uf8ff")
                .addValueEventListener(getRelationValueListener(db, adapter, false));
    }

    public static void getRecipesByType(String typeMyRecipes, CardAdapter adapter) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(CATEGORIES_REF).orderByChild("categoryId")
                .equalTo(typeMyRecipes)
                .addValueEventListener(getRelationValueListener(db, adapter, true));
    }

    public static void addUserCategoryIfNeed(String categoryName, List<Category> categories) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query q = ref.child(CATEGORIES_REF)
                .orderByChild("categoryId")
                .startAt(categoryName)
                .endAt(categoryName + "\uf8ff")
                .limitToFirst(1);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    categories.add(new Category("https://data.whicdn.com/images/303393554/large.jpg",
                            "user recipes", "пользовательские рецепты", categoryName, Utils.TYPE.TYPE_MY_RECIPES));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static ValueEventListener getRelationValueListener(DatabaseReference db, CardAdapter cardAdapter, boolean insert) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> ids = new ArrayList<>();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    ids.add(shot.getValue(RelationCategoryRecipe.class).getRecipeId());
                }

                if (ids.size() == 0)
                    return;

                Query q = db.child(PREVIEW_REF).orderByChild("id");
                for (String id : ids) {
                    q.equalTo(id);
                }

                q.addValueEventListener(getPreviewValueListener(insert, cardAdapter));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public static ValueEventListener getPreviewValueListener(boolean insert, CardAdapter adapter) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<RecipePrev> prevs = new ArrayList<>();
                for (DataSnapshot prevSnapshot : dataSnapshot.getChildren()) {
                    RecipePrev in = prevSnapshot.getValue(RecipePrev.class);
                    prevs.add(in);
                }

                if (insert) {
                    adapter.addData(prevs);
                } else {
                    adapter.setData(prevs);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public static void searchRecipes(ArrayList<String> categories, CardAdapter adapter) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query q = ref.child(CATEGORIES_REF).orderByChild("categoryId");
        for (String id : categories) {
            q.equalTo(id);
        }
        q.addValueEventListener(getRelationValueListener(ref, adapter, true));
    }

    /**
     * Redundant function in order not to translate whole set of ingredients
     */
    public static void updateIngredients() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("ingredients_ref").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    try {
                        IngredientFake in = shot.getValue(IngredientFake.class);
                        translate(in, ref.child("ingredients"));
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static int a = 0;

    public static void translate(IngredientFake ingredient, DatabaseReference ref) {
        if (!ingredient.getRu().equals("")) {
            ref.push().setValue(ingredient);
            return;
        }

        String text = ingredient.getEn();
        Call<TranslateResponse> call = App.getTranslateApi().translate(text, TranslateAPI.EN_RU);
        Thread t = new Thread(() -> {
            Response<TranslateResponse> response = null;
            try {
                response = call.execute();
                String ru = response.body().text.get(0);
                ingredient.setRu(ru);
                ref.push().setValue(ingredient);
                a++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        t.start();
    }
}
