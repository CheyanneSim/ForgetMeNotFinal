package com.example.forgetMeNot.shoppingList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forgetMeNot.R;
import com.example.forgetMeNot.Authentication.UserDetails;
import com.example.forgetMeNot.SharingData.GroupFragment;
import com.example.forgetMeNot.necessities.Necessity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

// TODO Firebase path wrong, should be Groups, GroupID, Necessities...
// Edit MyNecessities & My shopping list file!

public class MyShoppingList extends AppCompatActivity implements AddToShoppingList.DialogListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionRef = db.collection("Necessities");
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> selectedItems = new ArrayList<>();
    ListView shoppingList;

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

        // Adding items from firebase that are "Not Available" into shoppingList
        collectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                boolean isAvailable = (boolean) doc.getData().get(Necessity.availabilityKey);
                                if (!isAvailable) {
                                    String item = (String) doc.getData().get(Necessity.itemKey);
                                    items.add(item);
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

    public void removePurchased(View view) {
        // TODO add selected items to inventory list
        for (String item : selectedItems) {
            items.remove(item);
            collectionRef.document(item).update("Availability", true);
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
    public void addItem(String item) {
        items.add(item);
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