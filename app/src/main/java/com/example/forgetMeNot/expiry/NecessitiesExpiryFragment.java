package com.example.forgetMeNot.expiry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.forgetMeNot.Inventory.Food;
import com.example.forgetMeNot.R;
import com.example.forgetMeNot.necessities.Necessity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.forgetMeNot.SharingData.GroupFragment.GROUP;
import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class NecessitiesExpiryFragment extends Fragment implements EditExpiryDialog.DialogListener{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference necessitiesCollectionRef;
    public String group;
    ExpandableListView expandableListView;
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<Food> foods = new ArrayList<>();
    HashMap<String, List<String>> hashMap = new HashMap<>();

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Necessities Expiry Tracker");

        expandableListView = (ExpandableListView) getActivity().findViewById(R.id.necessities_expandable);
        expandableListView.setGroupIndicator(null);

        // Load group from GroupFragment
        loadGroup();
        necessitiesCollectionRef = db.collection("Groups").document(group).collection("Necessities");

        setListView();

        // Listview on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String item = (String) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                EditExpiryDialog dialog = new EditExpiryDialog(item);
                dialog.setTargetFragment(NecessitiesExpiryFragment.this,1);
                dialog.setStyle(EditExpiryDialog.STYLE_NORMAL, R.style.CustomDialog);
                dialog.show(getFragmentManager(), "Edit Expiry Date");
                return false;
            }
        });
    }

    // Retrieve group name from GroupFragment using shared preferences
    public void loadGroup() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        group = sharedPreferences.getString(GROUP, "");
    }

    public NecessitiesExpiryFragment() {}

    public void setListView() {
        dates.clear();
        foods.clear();
        hashMap.clear();
        necessitiesCollectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                boolean isAvailable = (boolean) doc.getData().get(Necessity.availabilityKey);
                                if (doc.contains(Necessity.expiryKey) && isAvailable) {
                                    String item = (String) doc.getData().get(Necessity.itemKey);
                                    String expiry = (String) doc.getData().get(Necessity.expiryKey);
                                    Food necessity = new Food(item, expiry, isAvailable);
                                    foods.add(necessity);
                                    if (!expiry.equals("") && !dates.contains(expiry)) {
                                        dates.add(expiry);
                                    } else if (expiry.equals("") && !dates.contains("No Expiry")) {
                                        dates.add("No Expiry");
                                    }
                                }
                            }

                            for (String date : dates) {
                                List<String> items = new ArrayList<>();
                                for (Food food : foods) {
                                    String expiry = food.getExpiry();
                                    if (expiry.equals("") && date.equals("No Expiry")) {
                                        String name = food.getFood();
                                        items.add(name);
                                    } else if (expiry != null && expiry.equals(date)) {
                                        String name = food.getFood();
                                        items.add(name);
                                    }
                                }
                                hashMap.put(date, items);
                            }

                            ExpiryAdapter adapter = new ExpiryAdapter(getContext(), dates, hashMap);
                            expandableListView.setAdapter(adapter);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.necessities_expiry_fragment, container, false);

        return view;
    }

    @Override
    public void delete(String item) {
        // Update firebase
        necessitiesCollectionRef.document(item).update("Availability", false);
        necessitiesCollectionRef.document(item).update("Expiry Date", "");

        // Update listview
        setListView();
    }

    @Override
    public void update(String item, String expiry) {
        //Update firebase
        necessitiesCollectionRef.document(item).update("Expiry Date", expiry);

        //Update listview
        setListView();
    }
}
