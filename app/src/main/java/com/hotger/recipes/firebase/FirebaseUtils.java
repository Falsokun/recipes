package com.hotger.recipes.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.hotger.recipes.Fake;
import com.hotger.recipes.utils.AppDatabase;
import com.hotger.recipes.model.Ingredient;
import com.hotger.recipes.model.Category;
import com.hotger.recipes.utils.AsyncCalls;

import java.util.ArrayList;

/**
 * Firebase realtime DB
 */
//TODO: пора подключать liveData видимо
public class FirebaseUtils {

    public static final String CATEGORY = "categories";
    public static final String NO_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/falsorecipes.appspot.com/o/storage%2Fno_img.png?alt=media&token=ec61e886-86f7-43d8-a4f4-a609477dd509";

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
                for (DataSnapshot jobSnapshot: dataSnapshot.getChildren()) {
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

    public static void saveCategoryToDatabase(AppDatabase db, String reference) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(reference);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Category>> t = new GenericTypeIndicator<ArrayList<Category>>() {};
                ArrayList<Category> categories = dataSnapshot.getValue(t);
                db.getCategoryDao().insertAll(categories);
                saveToDatabase(db);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private static void saveToDatabase(AppDatabase db) {
        for (Category category : db.getCategoryDao().getAllCategories()) {
            AsyncCalls.saveCategoryToDB(db, category.getSearchValue());
        }
    }

    //---------------------------------------
    /**
     * Для начальной инициализации с уникальными ключами
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
                GenericTypeIndicator<ArrayList<Fake>> t = new GenericTypeIndicator<ArrayList<Fake>>() {};
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

    public static Ingredient getIngredientById(DataSnapshot snapshot, String id) {
        Ingredient in = snapshot.child("-L7Tw_7UW9_EPy-nxATK").getValue(Ingredient.class);
        return in;
    }

    public static void addNewToDatabase(String en, String ru, String measure, FirebaseDatabase database) {
        DatabaseReference ref = database.getReference("ingredients").push();
        ref.setValue(new Ingredient(ref.getKey(), en, ru, measure));
    }
}
