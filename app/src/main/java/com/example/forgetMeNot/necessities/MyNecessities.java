package com.example.forgetMeNot.necessities;

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
import com.example.forgetMeNot.Notification.Alarm;
import com.example.forgetMeNot.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import static com.example.forgetMeNot.SharingData.GroupFragment.GROUP;
import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class MyNecessities extends AppCompatActivity implements AddToNecessities.DialogListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference necessitiesCollectionRef;
    public String group;
    public NecessitiesAdapter adapter;

    ArrayList<Necessity> necessities = new ArrayList<>();
    SwipeMenuListView listView;

    // To keep track of what the user has already keyed in
    ArrayList<String> inList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_necessities);

        // for action bar at the top of the screen
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("My Necessities");

        // Load group from GroupFragment
        loadGroup();
        necessitiesCollectionRef = db.collection("Groups").document(group).collection("Necessities");

        // Creating list of necessities
        listView = (SwipeMenuListView) findViewById(R.id.necessity_list);
        retrieveData();

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
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

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                delete(position);
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void delete(int position) {
        String item = necessities.get(position).getName();
        necessities.remove(position);
        inList.remove(item.trim().toLowerCase());
        necessitiesCollectionRef.document(item).delete();
        adapter = new NecessitiesAdapter(MyNecessities.this, R.layout.necessities_rowlayout, necessities);
        listView.setAdapter(adapter);
        // Cancel alarms
        Alarm.cancelAlarm(getApplicationContext(), item.hashCode());
        Alarm.cancelAlarm(getApplicationContext(), item.hashCode() * 2);

        Toast.makeText(getApplicationContext(), item + " deleted from your necessities", Toast.LENGTH_LONG).show();
    }


    //Retrieve group name from GroupFragment using shared preferences
    public void loadGroup() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        group = sharedPreferences.getString(GROUP, "");
    }

    // To retrieve data from firebase
    public void retrieveData() {
        necessitiesCollectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                String item = doc.getString(Necessity.itemKey);
                                boolean isAvailable = doc.getBoolean(Necessity.availabilityKey);
                                Necessity necessity;
                                if (doc.contains(Necessity.expiryKey)) {
                                    Date expiry = doc.getDate(Necessity.expiryKey);
                                    necessity = new NecessityFood(item, expiry, isAvailable);
                                } else {
                                    necessity = new NecessityNonFood(item, isAvailable);
                                }
                                necessities.add(necessity);
                                inList.add(item.trim().toLowerCase());
                                adapter = new NecessitiesAdapter(MyNecessities.this, R.layout.necessities_rowlayout, necessities);
                                listView.setAdapter(adapter);
                            }
                        }
                    }
                });
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
        AddToNecessities addToNecessities = new AddToNecessities();
        addToNecessities.show(getSupportFragmentManager(), "add to necessities");
    }


    // Set back button to finish activity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void addItem(String item, Date expiry, boolean isFood, boolean isAvailable) {
        if (inList.contains(item.trim().toLowerCase())) {
            Toast.makeText(getBaseContext(), "Item is already in your list", Toast.LENGTH_LONG).show();
        } else if (item == null || item.trim().equals("")) {
            Toast.makeText(getBaseContext(), "Input field is empty!", Toast.LENGTH_LONG).show();
        } else {
            //Adding to Firebase
            Necessity necessity;
            if (isFood) {
                necessity = new NecessityFood(item, expiry, isAvailable);
            } else {
                necessity = new NecessityNonFood(item, isAvailable);
            }
            necessity.createEntry(necessitiesCollectionRef);

            // To check whether item is already in necessities list
            inList.add(item.toLowerCase());

            // For List view
            necessities.add(necessity);
            adapter = new NecessitiesAdapter(MyNecessities.this, R.layout.necessities_rowlayout, necessities);
            listView.setAdapter(adapter);

            // Set 2 alarms - one 5 days before, one on the day itself
            if (expiry != null) {
                Alarm.setFirstAlarm(getApplicationContext(), expiry, item, true, item.hashCode());
                Alarm.setSecondAlarm(getApplicationContext(), expiry, item, true, item.hashCode());
            }
        }
    }
}