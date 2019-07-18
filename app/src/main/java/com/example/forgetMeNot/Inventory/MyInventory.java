package com.example.forgetMeNot.Inventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.forgetMeNot.R;
import com.example.forgetMeNot.necessities.Necessity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import static com.example.forgetMeNot.SharingData.GroupFragment.GROUP;
import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class MyInventory extends AppCompatActivity implements AddToInventory.DialogListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference necessitiesCollectionRef;
    private CollectionReference nonEssentialsCollectionRef;
    public String group;
    public final ArrayList<Item> arrayList = new ArrayList<>();
    private ItemListAdapter adapter;
    SwipeMenuListView listView;

    // To keep track of what the user has already keyed in
    ArrayList<String> inList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);

        listView = (SwipeMenuListView) findViewById(R.id.inventory_listView);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("My Inventory");

        // Load group from GroupFragment
        loadGroup();

        necessitiesCollectionRef = db.collection("Groups").document(group).collection("Necessities");
        nonEssentialsCollectionRef = db.collection("Groups").document(group).collection("Non-essentials");

        // Adding necessities from firebase that are "Available" into inventory
        necessitiesCollectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                boolean isAvailable = (boolean) doc.getData().get(Necessity.availabilityKey);
                                if (isAvailable) {
                                    String name = doc.getString(Necessity.itemKey);
                                    Date expiry;
                                    if (doc.contains(Necessity.expiryKey)) {
                                        expiry = doc.getDate("Expiry Date");
                                    } else {
                                        expiry = null;
                                    }
                                    Item item = new Item(name, expiry, false);
                                    arrayList.add(item);
                                    inList.add(name.trim().toLowerCase());
                                    adapter = new ItemListAdapter(MyInventory.this, R.layout.inventory_list_rowlayout, arrayList);
                                    listView.setAdapter(adapter);
                                }
                            }

                        }
                    }
                });

        // Adding non-essential food items from firebase into inventory
        nonEssentialsCollectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                String name = doc.getString(Food.itemKey);
                                Date expiry = doc.getDate(Food.expiryKey);
                                Item item = new Item(name, expiry, false);
                                arrayList.add(item);
                                inList.add(name.trim().toLowerCase());
                                adapter = new ItemListAdapter(MyInventory.this, R.layout.inventory_list_rowlayout, arrayList);
                                listView.setAdapter(adapter);
                            }
                        }
                    }
                });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                /*
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(170);
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
                */

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        listView.setMenuCreator(creator);

        // TODO open button
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //switch (index) {
                    //case 0:
                   /*     // open
                        break;
                    case 1:*/
                        // delete
                        delete(position);
                        //break;
                //}
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void delete(int position) {
        String item = arrayList.get(position).getName();
        arrayList.remove(position);
        inList.remove(item.toLowerCase());
        nonEssentialsCollectionRef.document(item).delete();
        necessitiesCollectionRef.document(item).update("Availability", false);
        adapter = new ItemListAdapter(MyInventory.this, R.layout.inventory_list_rowlayout, arrayList);
        listView.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), item + " deleted from your inventory", Toast.LENGTH_LONG).show();
    }

    //Retrieve group name from GroupFragment using shared preferences
    public void loadGroup() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        group = sharedPreferences.getString(GROUP, "");
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
        AddToInventory addToInventory = new AddToInventory();
        addToInventory.show(getSupportFragmentManager(), "add to inventory");
    }

    // Set back button to finish activity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // To add non-essential food items
    @Override
    public void addItem(String item, Date expiry) {
        if (inList.contains(item.trim().toLowerCase())) {
            Toast.makeText(getBaseContext(), "Item is already in your list", Toast.LENGTH_LONG).show();
        } else if (item == null || item.trim().equals("")) {
            Toast.makeText(getBaseContext(), "Input field is empty!", Toast.LENGTH_LONG).show();
        } else {
            //Adding to Firebase
            Food food = new Food(item, expiry, true);
            food.createEntry(nonEssentialsCollectionRef);

            // To check whether item is already in necessities list
            inList.add(item.toLowerCase());

            // For List view
            Item toAdd = new Item(item, expiry, false);
            arrayList.add(toAdd);
            adapter = new ItemListAdapter(MyInventory.this, R.layout.inventory_list_rowlayout, arrayList);
            listView.setAdapter(adapter);
        }
    }
}
