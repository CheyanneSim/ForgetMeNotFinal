package com.example.forgetMeNot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.forgetMeNot.Inventory.MyInventory;
import com.example.forgetMeNot.SharingData.GroupFragment;
import com.example.forgetMeNot.expiry.ExpiryTracker;
import com.example.forgetMeNot.necessities.MyNecessities;
import com.example.forgetMeNot.shoppingList.MyShoppingList;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    TextView userEmailTV;
    Button necessitiesBtn, shoppingListBtn, inventoryBtn, expiryBtn;


        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getActivity().setTitle("Home");
            mAuth = FirebaseAuth.getInstance();

            userEmailTV = view.findViewById(R.id.userEmail);

            necessitiesBtn = view.findViewById(R.id.necessities_button);
            shoppingListBtn = view.findViewById(R.id.shopping_list_button);
            inventoryBtn = view.findViewById(R.id.inventory_btn);
            expiryBtn = view.findViewById(R.id.expiry_btn);

            // on logged in
            if(mAuth.getCurrentUser()!=null) {
                userEmailTV.setText("Welcome " + mAuth.getCurrentUser().getDisplayName());
            }

            // Let user join / create group before they begin!
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            if (!sharedPreferences.contains(GroupFragment.GROUP)) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new GroupFragment());
                ft.commit();
            }

            // Move to Inventory List on button press
            inventoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getContext(), MyInventory.class);

                    startActivityForResult(intent, 0);
                }
            });

            // Move to shopping list on button press
            shoppingListBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getContext(), MyShoppingList.class);

                    startActivityForResult(intent, 0);

                }
            });

            // Move to Expiry Tracker on button press
            expiryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ExpiryTracker.class);

                    startActivityForResult(intent, 0);
                }
            });


            // Move to Necessities on button press
            necessitiesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getContext(), MyNecessities.class);

                    startActivityForResult(intent, 0);
                }
            });

        }

        public HomeFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_home, container, false);

        }


    }
