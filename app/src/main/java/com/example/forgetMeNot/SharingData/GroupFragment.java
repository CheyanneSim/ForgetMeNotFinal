package com.example.forgetMeNot.SharingData;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forgetMeNot.Authentication.UserDetails;
import com.example.forgetMeNot.HomeFragment;
import com.example.forgetMeNot.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionRef = db.collection("Groups");
    private String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    public static final String grpKey = "Groups";
    private String groupName;
    private TextView groupTextView;
    private TextView noGroupTextView;
    private EditText existingGrp;
    private EditText newGrp;
    private EditText existingpw;
    private EditText newpw;
    private Button joinGrp;
    private Button createGrp;
    ArrayList<String> existingGroups = new ArrayList<>();

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String GROUP = "group";

    public GroupFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Manage Groups");

        joinGrp = getActivity().findViewById(R.id.join_grp_btn);
        createGrp = getActivity().findViewById(R.id.create_grp_btn);
        groupTextView = getActivity().findViewById(R.id.group_name_textView);
        noGroupTextView = getActivity().findViewById(R.id.nogroup_tv);

        loadGroup();

        // Retrieving all group names from Firestore
        collectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                String name = (String) doc.getId();
                                existingGroups.add(name);
                            }
                        }
                    }
                });

        joinGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group;
                existingGrp = getActivity().findViewById(R.id.existing_group_editText);
                existingpw = getActivity().findViewById(R.id.existingpw_editText);
                group = existingGrp.getText().toString();
                checkValidity(group, existingpw.getText().toString());
                existingGrp.setText("");
            }
        });

        createGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group;
                newGrp = getActivity().findViewById(R.id.new_group_edit_text);
                newpw = getActivity().findViewById(R.id.newpw_editText);
                group = newGrp.getText().toString();
                createGroup(group, newpw.getText().toString());
                newGrp.setText("");
            }
        });

    }

    private void createGroup(String group, String password) {
        if (group.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), "Please input a group name!", Toast.LENGTH_LONG).show();
        } else if (existingGroups.contains(group)) {
            Toast.makeText(getActivity().getApplicationContext(), "This group name is already taken, pick another one!", Toast.LENGTH_LONG).show();
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put(email, "Member");
            data.put("Password", password);
            db.collection(grpKey).document(group).set(data);
            saveGroup(group);
            updateView();

            // After creating group, transit to Home page.
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new HomeFragment());
            ft.commit();
        }
    }

    private void checkValidity(final String group, final String password) {
        if (group.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), "Please input the group name!", Toast.LENGTH_LONG).show();
        } else if (group.equals(groupTextView.getText().toString())) {
            Toast.makeText(getActivity().getApplicationContext(), "You are already in the group!", Toast.LENGTH_LONG).show();
        } else if (existingGroups.contains(group)) {
            db.collection(grpKey).document(group).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String correctpw = documentSnapshot.getString("Password");
                        if (correctpw.equals(password)) {
                            joinGroup(group);
                        } else {
                            Toast.makeText(getContext(), "Incorrect Password", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "There is no such group. Create a new group below!", Toast.LENGTH_LONG).show();
        }
    }


    private void joinGroup(String group) {
        Map<String, Object> data = new HashMap<>();
        data.put(email, "Member");
        db.collection(grpKey).document(group).set(data, SetOptions.merge());
        saveGroup(group);
        updateView();

        // After joining group, transit to Home page
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new HomeFragment());
        ft.commit();
    }

    // Using SharedPreferences to save group
    public void saveGroup(String group) {
        groupName = group;

        // Save to Firestore
        email = mAuth.getCurrentUser().getEmail();
        Map<String, Object> data = new HashMap<>();
        data.put("Group", group);
        db.collection(UserDetails.userDetailsKey).document(email).set(data);

        // Save to sharedPreferences
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GROUP, group);
        editor.apply();
        Toast.makeText(this.getActivity(), "Group saved", Toast.LENGTH_LONG).show();
    }

    public void loadGroup() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        groupName = sharedPreferences.getString(GROUP, "");
        updateView();
        if (groupName.equals("")) {
            noGroupTextView.setText("Please join a group before you begin!");
        }
    }

    public void updateView() {
        groupTextView.setText(groupName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);

        return view;
    }
}
