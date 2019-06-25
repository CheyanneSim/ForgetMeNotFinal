package com.example.forgetMeNot.expiry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.forgetMeNot.Inventory.Food;
import com.example.forgetMeNot.R;
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

public class NonEssentialsExpiryFragment extends Fragment implements EditExpiryDialog.DialogListener{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference nonEssentialCollectionRef;
    public String group;
    ExpandableListView expandableListView;
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<Food> foods = new ArrayList<>();
    HashMap<String, List<String>> hashMap = new HashMap<>();

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Non-essentials Expiry Tracker");

        expandableListView = (ExpandableListView) getActivity().findViewById(R.id.nonessentials_expandable);
        expandableListView.setGroupIndicator(null);

        // Load group from GroupFragment
        loadGroup();
        nonEssentialCollectionRef = db.collection("Groups").document(group).collection("Non-essentials");

        setListView();

        // Listview on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String item = (String) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                EditExpiryDialog dialog = new EditExpiryDialog(item);
                dialog.setTargetFragment(NonEssentialsExpiryFragment.this,1);
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

    public NonEssentialsExpiryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.nonessentials_expiry_fragment, container, false);

        return view;
    }

    public void setListView() {
        dates.clear();
        foods.clear();
        hashMap.clear();
        nonEssentialCollectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                String item = (String) doc.getData().get(Food.itemKey);
                                String expiry = (String) doc.getData().get(Food.expiryKey);
                                Food necessity = new Food(item, expiry, true);
                                foods.add(necessity);
                                if (!dates.contains(expiry)) {
                                    dates.add(expiry);
                                }
                            }
                            for (String date : dates) {
                                List<String> items = new ArrayList<>();
                                for (Food food : foods) {
                                    String expiry = food.getExpiry();
                                    String name = food.getFood();
                                    if (expiry.equals(date)) {
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
    public void delete(String item) {
        nonEssentialCollectionRef.document(item).delete();
        setListView();
    }

    @Override
    public void update(String item, String expiry) {
        nonEssentialCollectionRef.document(item).update("Expiry Date", expiry);
        setListView();
    }
}
