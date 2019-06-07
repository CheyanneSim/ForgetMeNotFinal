package com.example.forgetMeNot.SharingData;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forgetMeNot.Authentication.UserDetails;
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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionRef = db.collection("Groups");
    private String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    public static final String grpKey = "Groups";
    private TextView groupName;
    private EditText existingGrp;
    private EditText newGrp;
    private Button joinGrp;
    private Button createGrp;
    ArrayList<String> existingGroups = new ArrayList<>();

    public GroupFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Manage Groups");

        groupName = getActivity().findViewById(R.id.group_name_textView);
        db.collection(UserDetails.userDetailsKey).document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            groupName.setText((String)documentSnapshot.get("Group"));
                        }
                    }
                });

        joinGrp = getActivity().findViewById(R.id.join_grp_btn);
        createGrp = getActivity().findViewById(R.id.create_grp_btn);

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
                group = existingGrp.getText().toString();
                joinGroup(group);
                existingGrp.setText("");
            }
        });

        createGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group;
                newGrp = getActivity().findViewById(R.id.new_group_edit_text);
                group = newGrp.getText().toString();
                createGroup(group);
                newGrp.setText("");
            }
        });

    }

    private void createGroup(String group) {
        if (existingGroups.contains(group)) {
            Toast.makeText(getActivity().getApplicationContext(), "This group name is already taken, pick another one!", Toast.LENGTH_LONG).show();
        } else {
            groupName.setText(group);
            Map<String, Object> data = new HashMap<>();
            data.put(email, "Member");
            db.collection(grpKey).document(group).set(data);
            Map<String, Object> userData = new HashMap<>();
            userData.put("Group", group);
            db.collection(UserDetails.userDetailsKey).document(email).set(userData);
        }
    }

    private void joinGroup(String group) {
        if (group.equals(groupName.getText().toString())) {
            Toast.makeText(getActivity().getApplicationContext(), "You are already in the group!", Toast.LENGTH_LONG).show();
        } else if (existingGroups.contains(group)) {
            groupName.setText(group);
            Map<String, Object> data = new HashMap<>();
            data.put(email, "Member");
            db.collection(grpKey).document(group).set(data, SetOptions.merge());
            Map<String, Object> userData = new HashMap<>();
            userData.put("Group", group);
            db.collection(UserDetails.userDetailsKey).document(email).set(userData);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "There is no such group. Create a new group below!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);

        return view;
    }
}
