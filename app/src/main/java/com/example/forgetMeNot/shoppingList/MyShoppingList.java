package com.example.forgetMeNot.shoppingList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forgetMeNot.Inventory.MyInventory;
import com.example.forgetMeNot.R;
import com.example.forgetMeNot.SharingData.GroupFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.forgetMeNot.SharingData.GroupFragment.GROUP;
import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class MyShoppingList extends AppCompatActivity implements AddToShoppingList.DialogListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference necessitiesCollectionRef;
    private CollectionReference extraShoppingListCollection;
    private CollectionReference nonEssentialsCollectionRef;
    public String group;
    public ArrayAdapter<String> adapter;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> selectedItems = new ArrayList<>();
    ListView shoppingList;

    public MyShoppingList(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        Toolbar shopping_list_toolbar = findViewById(R.id.shopping_list_toolbar);
        setSupportActionBar(shopping_list_toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("My Shopping List");

        shoppingList = (ListView) findViewById(R.id.shopping_list);
        shoppingList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Load group from GroupFragment
        loadGroup();

        necessitiesCollectionRef = db.collection("Groups").document(group).collection("Necessities");
        nonEssentialsCollectionRef = db.collection("Groups").document(group).collection("Non-essentials");
        extraShoppingListCollection = db.collection("Groups").document(group).collection("Shopping List");

        // retrieve extra shopping list that user added from Firebase
        extraShoppingListCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                String item = doc.getString("Item");
                                items.add(item);
                            }
                            adapter = new ArrayAdapter<String>(MyShoppingList.this, R.layout.shoppinglist_row_layout, R.id.checked_txt, items);
                            shoppingList.setAdapter(adapter);
                        }
                    }
                });

        // Adding necessities that has ran out
        necessitiesCollectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                boolean isAvailable = doc.getBoolean("Availability");
                                if (!isAvailable) {
                                    String item = doc.getString("Necessity");
                                    if (!items.contains(item)) {
                                        items.add(item);
                                    }
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyShoppingList.this, R.layout.shoppinglist_row_layout, R.id.checked_txt, items);
                            shoppingList.setAdapter(adapter);
                        }
                    }
                });

        // Removing selected items from shopping list
        shoppingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView)view).getText().toString();
                if (selectedItems.contains(selectedItem)) {
                    selectedItems.remove(selectedItem);
                } else {
                    selectedItems.add(selectedItem);
                }
            }
        });
    }

    //Retrieve group name from GroupFragment using shared preferences
    public void loadGroup() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        group = sharedPreferences.getString(GROUP, "");
    }

    //TODO require user to input expiry immediately
    public void removePurchased(View view) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String item : selectedItems) {
            items.remove(item);
            necessitiesCollectionRef.document(item).update("Availability", true);
            necessitiesCollectionRef.document(item).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("Expiry Date")) {
                            documentSnapshot.getReference().update("Expiry Date", null);
                        }
                    }
                }
            });

            // If it is non-essential food item, add it to My Inventory, then delete from Shopping List
            extraShoppingListCollection.document(item).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("Item");
                        boolean food = documentSnapshot.getBoolean("Is Food");
                        if (food) {
                            // Add to Firestore under Non-essentials
                            Map<String, Object> data = new HashMap<>();
                            data.put("Availability", true);
                            data.put("Expiry Date", null);
                            data.put("Food", name);
                            nonEssentialsCollectionRef.document(name).set(data);
                            Log.d("My tag", "added to inventory");
                        }
                    }
                }
            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("Item");
                        extraShoppingListCollection.document(name).delete();
                    }
                }
            });

            // To off the purchase switch in inventory
            editor.putBoolean(item, false);
            editor.apply();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyShoppingList.this, R.layout.shoppinglist_row_layout, R.id.checked_txt, items);
        shoppingList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adding_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_btn) {
            openDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialog() {
        AddToShoppingList addToShoppingList = new AddToShoppingList();
        addToShoppingList.show(getSupportFragmentManager(), "add to shopping list");
    }

    @Override
    public void addItem(String item, boolean isFood) {
        if (!items.contains(item)) {
            // Add to firestore
            Map<String, Object> data = new HashMap<>();
            data.put("Item", item);
            data.put("Is Food", isFood);
            extraShoppingListCollection.document(item).set(data);
            // add to list
            items.add(item);
        } else {
            Toast.makeText(getApplicationContext(), "Item is already in your shopping list!", Toast.LENGTH_LONG).show();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyShoppingList.this, R.layout.shoppinglist_row_layout, R.id.checked_txt, items);
        shoppingList.setAdapter(adapter);
    }


    // Set back button to finish activity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}