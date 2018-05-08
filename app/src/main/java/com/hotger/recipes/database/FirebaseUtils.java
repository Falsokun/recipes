package com.hotger.recipes.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hotger.recipes.App;
import com.hotger.recipes.adapter.CardAdapter;
import com.hotger.recipes.database.RelationCategoryRecipe;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.model.Ingredient;
import com.hotger.recipes.model.Product;
import com.hotger.recipes.model.Recipe;
import com.hotger.recipes.model.RecipeNF;
import com.hotger.recipes.model.RecipePrev;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.utils.AsyncCalls;
import com.hotger.recipes.utils.TranslateAPI;
import com.hotger.recipes.utils.Utils;
import com.hotger.recipes.view.ControllableActivity;
import com.hotger.recipes.view.RecipeFragment;
import com.hotger.recipes.utils.TranslateResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
    public static final String INGREDIENT_REF = "ingredient_ref";
    private static final String PREVIEW_REF = "preview_ref";
    public static final String CATEGORY_REF_SEND = "category_ref_send";
    public static final String RECIPE_REF_EXTRA = "recipe_ref_extra";

    public static void saveIngredientsToDatabase(AppDatabase db) {
        final FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference ref = database.collection(INGREDIENT_REF);

        // Attach a listener to read the data at our posts reference
        ref.get().addOnCompleteListener(task -> {
            //надо попробовать
//                GenericTypeIndicator<HashMap<String, Object>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, Object>>() {};
//                Map<String, Object> objectHashMap = dataSnapShot.getValue(objectsGTypeInd);
//                ArrayList<Object> objectArrayList = new ArrayList<Object>(objectHashMap.values());
            if (!task.isSuccessful())
                return;

            ArrayList<Ingredient> ingredients = new ArrayList<>();
            for (QueryDocumentSnapshot jobSnapshot : task.getResult()) {
                Ingredient in = jobSnapshot.toObject(Ingredient.class);
                ingredients.add(in);
            }

            db.getIngredientDao().insertAll(ingredients);
        });
    }

    public static void saveCategoryToDatabase(Context context, AppDatabase db, String reference) {
        final FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference ref = database.collection(CATEGORIES_REF);
        //reference
        // Attach a listener to read the data at our posts reference
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful())
                return;

            ArrayList<Category> categories = new ArrayList<>();
            for (QueryDocumentSnapshot jobSnapshot : task.getResult()) {
                Category in = jobSnapshot.toObject(Category.class);
                categories.add(in);
            }

            db.getCategoryDao().insertAll(categories);
            saveToDatabase(context);
        });
    }

    private static void saveToDatabase(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        for (Category category : db.getCategoryDao().getAllCategories()) {
            AsyncCalls.saveCategoryToDB(context, category.getSearchValue(), false);
        }
    }

    //---------------------------------------

//    /**
//     * Для начальной инициализации с уникальными ключами
//     *
//     * @param db
//     */
//    public static void initDatabase(AppDatabase db) {
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference("ingredients");
//
//        // Attach a listener to read the data at our posts reference
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                final FirebaseDatabase database = FirebaseDatabase.getInstance();
//                GenericTypeIndicator<ArrayList<Fake>> t = new GenericTypeIndicator<ArrayList<Fake>>() {
//                };
//                ArrayList<Fake> ingredients = dataSnapshot.getValue(t);
//                for (Fake fake : ingredients) {
//                    addNewToDatabase(fake.getEn(), fake.getRu(), fake.getMeasure(), database);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });
//    }
//
//    public static void addNewToDatabase(String en, String ru, String measure, FirebaseDatabase database) {
//        DatabaseReference ref = database.getReference("ingredients").push();
//        ref.setValue(new Ingredient(en, ru, measure));
//    }

    public static void saveRecipeToFirebase(Recipe recipe, List<RelationCategoryRecipe> relations,
                                            RecipePrev prev) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RECIPES_REF).add(recipe.getRecipe());
        CollectionReference productsRef = db.collection(PRODUCTS_REF);
        CollectionReference categoryRef = db.collection(CATEGORIES_REF);
        db.collection(PREVIEW_REF).add(prev);
        for (Product p : recipe.getProducts()) {
            productsRef.add(p);
        }

        for (RelationCategoryRecipe rel : relations) {
            categoryRef.add(rel);
        }
    }

    public static Recipe getRecipeFromFirebase(String id, ControllableActivity activity) {
        RecipeFragment fragment = new RecipeFragment();
        activity.setCurrentFragment(fragment, true, fragment.getTag());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(RECIPES_REF).whereEqualTo("id", id).limit(1);
        query.get().addOnCompleteListener(task -> {
            RecipeNF recipenf = null;
            ArrayList<Category> categories = new ArrayList<>();
            for (QueryDocumentSnapshot shot : task.getResult()) {
                recipenf = shot.toObject(RecipeNF.class);
            }

            Recipe recipe = new Recipe(recipenf);

            db.collection(CATEGORIES_REF).whereEqualTo("recipeId", id).get().addOnCompleteListener(getOnCompleteListener(new Product(), activity));
            db.collection(PRODUCTS_REF).whereEqualTo("recipeId", id).get().addOnCompleteListener(getOnCompleteListener(new Product(), activity));

            Intent intent = new Intent(Utils.RECIPE_OBJ);
            intent.putExtra(Utils.RECIPE_OBJ, recipe);
            intent.putExtra(RECIPES_REF, true);
            LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
        });
        return null;
    }

    public static <T> OnCompleteListener<QuerySnapshot> getOnCompleteListener(T cl, ControllableActivity activity) {
        return task -> {
            ArrayList<T> values = new ArrayList<>();
            for (QueryDocumentSnapshot shot : task.getResult()) {
                values.add((T) shot.toObject(cl.getClass()));
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
        };
    }

    public static void getAllRecipesInCategory(String typeMyRecipes, CardAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CATEGORIES_REF).orderBy("categoryId")
                .whereLessThan("categoryId", typeMyRecipes)
                .whereGreaterThan("categoryId",typeMyRecipes + "\uf8ff")
                .get()
                .addOnCompleteListener(getRelationValueListener(db, adapter, false));
    }

    public static void getRecipesByType(String typeMyRecipes, CardAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CATEGORIES_REF).whereEqualTo("categoryId", typeMyRecipes).get()
                .addOnCompleteListener(getRelationValueListener(db, adapter, true));
    }

    public static void addUserCategoryIfNeed(String categoryName, List<Category> categories) {
        FirebaseFirestore ref = FirebaseFirestore.getInstance();
        Query q = ref.collection(CATEGORIES_REF)
                .whereLessThan("categoryId", categoryName)
                .whereGreaterThan("categoryId",categoryName + "\uf8ff")
                .limit(1);
        q.get().addOnCompleteListener(task -> {
            if (task.getResult().size() > 0) {
                categories.add(new Category("https://data.whicdn.com/images/303393554/large.jpg",
                        "user recipes", "пользовательские рецепты", categoryName, Utils.TYPE.TYPE_MY_RECIPES));
            }
        });
    }

    public static OnCompleteListener<QuerySnapshot> getRelationValueListener(FirebaseFirestore db, CardAdapter cardAdapter, boolean insert) {
        return task -> {
            List<String> ids = new ArrayList<>();
            for (QueryDocumentSnapshot shot : task.getResult()) {
                ids.add(shot.toObject(RelationCategoryRecipe.class).getRecipeId());
            }

            if (ids.size() == 0)
                return;

            Query q = db.collection(PREVIEW_REF);
            for (String id : ids) {
                q.whereEqualTo("id", id);
            }

            q.get().addOnCompleteListener(getPreviewValueListener(insert, cardAdapter));
        };
    }

    public static OnCompleteListener<QuerySnapshot> getPreviewValueListener(boolean insert, CardAdapter adapter) {
        return task -> {
            ArrayList<RecipePrev> prevs = new ArrayList<>();
            for (QueryDocumentSnapshot prevSnapshot : task.getResult()) {
                RecipePrev in = prevSnapshot.toObject(RecipePrev.class);
                prevs.add(in);
            }

            if (insert) {
                adapter.addData(prevs);
            } else {
                adapter.setData(prevs);
            }
        };
    }

    public static void searchRecipes(ArrayList<String> categories, CardAdapter adapter) {
        FirebaseFirestore ref = FirebaseFirestore.getInstance();
        Query q = ref.collection(CATEGORIES_REF);
        for (String id : categories) {
            q.whereEqualTo("categoryId", id);
        }

        q.get().addOnCompleteListener(getRelationValueListener(ref, adapter, true));
    }

    /**
     * Redundant function in order not to translate whole set of ingredients
     */
    public static void updateIngredients() {
        FirebaseFirestore ref = FirebaseFirestore.getInstance();
        ref.collection("ingredients_ref").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot shot : task.getResult()) {
                    try {
                        Ingredient in = shot.toObject(Ingredient.class);
                        translate(in, ref.collection("ingredients"));
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void translate(Ingredient ingredient, CollectionReference ref) {
        if (!ingredient.getRu().equals("")) {
            ref.add(ingredient);
            return;
        }

        String text = ingredient.getEn();
        Call<TranslateResponse> call = App.getTranslateApi().translate(text, TranslateAPI.EN_RU);
        Thread t = new Thread(() -> {
            Response<TranslateResponse> response;
            try {
                response = call.execute();
                String ru = response.body().text.get(0);
                ingredient.setRu(ru);
                ref.add(ingredient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        t.start();
    }

    public static void deleteAllDocuments() {
//        FirebaseFirestore.getInstance().collection("24").add();
    }

    public static void addCategoriesToFirestore(Activity activity) {
//        ArrayList<Category> list = loadJSONFromAsset(activity, R.raw.ref_categories, Category.class);
//        uploadToFirebase(list, CATEGORIES_REF);
//        ArrayList<Ingredient> ingredientlist = loadJSONFromAsset(activity, R.raw.ref_ingredients, Ingredient.class);
//        uploadToFirebase(ingredientlist, INGREDIENT_REF);
    }

    public static <T> ArrayList<T> loadJSONFromAsset(Activity activity, int rawId, Class<T> clazz) {
        ArrayList<T> list = new ArrayList<>();
        String json = null;
        try {
            InputStream is = activity.getResources().openRawResource(rawId);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONArray m_jArry = new JSONArray(json);
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo = m_jArry.getJSONObject(i);
                Gson gson = new GsonBuilder().create();
                T category = gson.fromJson(jo.toString(), clazz);
                list.add(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static <T> void uploadToFirebase(ArrayList<T> list, String collection) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference ref;
        for(int i = 0; i < list.size(); i++) {
            ref = firestore.collection(collection);
            if (list.get(i) instanceof Ingredient) {
                translate((Ingredient) list.get(i), ref);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                ref.add(list.get(i));
            }
        }
    }
}
