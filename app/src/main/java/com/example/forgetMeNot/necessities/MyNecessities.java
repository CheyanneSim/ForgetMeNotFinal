package com.example.forgetMeNot.necessities;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.forgetMeNot.R;
import com.example.forgetMeNot.Authentication.UserDetails;
import com.example.forgetMeNot.SharingData.GroupFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyNecessities extends AppCompatActivity implements AddToNecessities.DialogListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionRef = db.collection("Necessities");
    public String group;

    ArrayList<Map<String,String>> necessities = new ArrayList<>();
    ListView show;

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

        // Creating list of necessities
        show = (ListView) findViewById(R.id.necessity_list);
        retrieveData();
    }

    // To retrieve data from firebase
    public void retrieveData() {
        collectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                Map<String, String> data = new HashMap<>();
                                String item = (String) doc.getData().get(Necessity.itemKey);
                                boolean isAvailable = (boolean) doc.getData().get(Necessity.availabilityKey);
                                data.put("Necessity", item);
                                if (isAvailable) {
                                    data.put("Availability", "Available");
                                } else {
                                    data.put("Availability", "Not Available");
                                }
                                necessities.add(data);
                            }
                            SimpleAdapter adapter = new SimpleAdapter(MyNecessities.this, necessities,
                                    android.R.layout.simple_list_item_2,
                                    new String[] {"Necessity", "Availability"},
                                    new int[] {android.R.id.text1,
                                            android.R.id.text2});
                            show.setAdapter(adapter);
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
    public void addItem(String item, boolean isFood, boolean isAvailable) {
        if (inList.contains(item.trim().toLowerCase())) {
            Toast.makeText(getBaseContext(), "Item is already in your list", Toast.LENGTH_LONG).show();
        } else if (item == null || item.trim().equals("")) {
            Toast.makeText(getBaseContext(), "Input field is empty!", Toast.LENGTH_LONG).show();
        } else {
            //Adding to Firebase
            Necessity necessity;
            if (isFood) {
                necessity = new NecessityFood(item, isAvailable);
            } else {
                necessity = new NecessityNonFood(item, isAvailable);
            }
            necessity.createEntry(collectionRef);

            // To check whether item is already in necessities list
            inList.add(item.toLowerCase());

            // For List view
            Map<String, String> data = new HashMap<>();
            data.put("Necessity", item);
            if (isAvailable) {
                data.put("Availability", "Available");
            } else {
                data.put("Availability", "Not Available");
            }
            necessities.add(data);
            SimpleAdapter adapter = new SimpleAdapter(MyNecessities.this, necessities,
                    android.R.layout.simple_list_item_2,
                    new String[]{"Necessity", "Availability"},
                    new int[]{android.R.id.text1,
                            android.R.id.text2});
            show.setAdapter(adapter);
        }
    }
}