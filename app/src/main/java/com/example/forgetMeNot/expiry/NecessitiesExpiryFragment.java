package com.example.forgetMeNot.expiry;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.forgetMeNot.Inventory.Food;
import com.example.forgetMeNot.Notification.Alarm;
import com.example.forgetMeNot.R;
import com.example.forgetMeNot.necessities.Necessity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.example.forgetMeNot.SharingData.GroupFragment.GROUP;
import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class NecessitiesExpiryFragment extends Fragment implements EditExpiryDialog.DialogListener{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference necessitiesCollectionRef;
    private SimpleDateFormat formatter;
    private AutoCompleteTextView search;
    private ImageButton cancel;
    public String group;
    ExpandableListView expandableListView;
    ArrayList<Date> dates = new ArrayList<>();
    ArrayList<String> headers = new ArrayList<>();
    ArrayList<Food> foods = new ArrayList<>();
    ArrayList<String> searches = new ArrayList<>();
    HashMap<String, List<String>> hashMap = new HashMap<>();

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Necessities Expiry Tracker");

        formatter = new SimpleDateFormat("dd/MM/yy");

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
                String currentExpiry = "No Expiry";
                for (Food food : foods) {
                    if (food.getFood().equals(item)) {
                        if (food.getExpiry() != null) {
                            currentExpiry = formatter.format(food.getExpiry());
                        }
                        break;
                    }
                }
                EditExpiryDialog dialog = new EditExpiryDialog(item, currentExpiry);
                dialog.setTargetFragment(NecessitiesExpiryFragment.this,1);
                dialog.setStyle(EditExpiryDialog.STYLE_NORMAL, R.style.CustomDialog);
                dialog.show(getFragmentManager(), "Edit Expiry Date");
                return false;
            }
        });

        search = view.findViewById(R.id.necessities_search);
        cancel = view.findViewById(R.id.cancel_btn);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, searches);
        search.setAdapter(adapter);
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchFood(search.getText().toString());
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
                setListView();
            }
        });
    }

    // Retrieve group name from GroupFragment using shared preferences
    public void loadGroup() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        group = sharedPreferences.getString(GROUP, "");
    }

    public NecessitiesExpiryFragment() {}

    public void searchFood(String search) {
        headers.clear();
        hashMap.clear();
        Date expiry;
        for (Food food : foods) {
            if (food.getFood().equals(search)) {
                expiry = food.getExpiry();
                if (expiry == null) {
                    headers.add("No Expiry");
                } else {
                    headers.add(formatter.format(expiry));
                }
                break;
            }
        }
        if (headers.isEmpty()) {
            Toast.makeText(getContext(), "Search not found", Toast.LENGTH_LONG).show();
        } else {
            List<String> items = new ArrayList<>();
            items.add(search);
            hashMap.put(headers.get(0), items);
        }

        ExpiryAdapter adapter = new ExpiryAdapter(getContext(), headers, hashMap);
        expandableListView.setAdapter(adapter);
    }

    public void setListView() {
        dates.clear();
        headers.clear();
        foods.clear();
        hashMap.clear();
        necessitiesCollectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                boolean isAvailable = (boolean) doc.getData().get(Necessity.availabilityKey);
                                if (isAvailable && doc.contains(Necessity.expiryKey)) {
                                    String item = doc.getString(Necessity.itemKey);
                                    Date expiry = doc.getDate(Necessity.expiryKey);
                                    Food necessity = new Food(item, expiry, isAvailable);
                                    foods.add(necessity);
                                    if (!dates.contains(expiry)) {
                                        dates.add(expiry);
                                    }
                                    searches.add(item);
                                }
                            }

                            //Handling food without expiry
                            if (dates.contains(null)) {
                                dates.remove(null);
                                headers.add("No Expiry");
                                List<String> items = new ArrayList<>();
                                for (Food food : foods) {
                                    Date expiry = food.getExpiry();
                                    if (expiry == null) {
                                        items.add(food.getFood());
                                    }
                                }
                                hashMap.put("No Expiry", items);
                            }

                            Collections.sort(dates);

                            for (Date date : dates) {
                                String header = formatter.format(date);
                                headers.add(header);
                                List<String> items = new ArrayList<>();
                                for (Food food : foods) {
                                    Date expiry = food.getExpiry();
                                    if (expiry != null && expiry.equals(date)) {
                                        items.add(food.getFood());
                                    }
                                }
                                hashMap.put(header, items);
                            }


                            ExpiryAdapter adapter = new ExpiryAdapter(getContext(), headers, hashMap);
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
        necessitiesCollectionRef.document(item).update("Expiry Date", null);

        // Update listview
        setListView();

        // Cancel alarms
        Alarm.cancelAlarm(getContext(), item.hashCode());
        Alarm.cancelAlarm(getContext(), item.hashCode() * 2);
    }

    @Override
    public void update(String item, Date expiry) {
        //Update firebase
        necessitiesCollectionRef.document(item).update("Expiry Date", expiry);

        //Update listview
        setListView();

        // Reset alarms
        Alarm.setFirstAlarm(getContext(), expiry, item, true, item.hashCode());
        Alarm.setSecondAlarm(getContext(), expiry, item, true, item.hashCode());
    }
}
