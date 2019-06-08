package com.example.forgetMeNot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.forgetMeNot.Inventory.MyInventory;
import com.example.forgetMeNot.necessities.MyNecessities;
import com.example.forgetMeNot.shoppingList.MyShoppingList;
import com.google.firebase.auth.FirebaseAuth;

// TODO Make sure user log in and join group before beginning to use APP!!!

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    TextView userEmailTV;
    Button necessitiesBtn, shoppingListBtn, inventoryBtn;


        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getActivity().setTitle("Home Screen");
            mAuth = FirebaseAuth.getInstance();



            userEmailTV = view.findViewById(R.id.userEmail);

            necessitiesBtn = view.findViewById(R.id.necessities_button);
            shoppingListBtn = view.findViewById(R.id.shopping_list_button);
            inventoryBtn = view.findViewById(R.id.inventory_btn);

            // TODO name takes too long to register!
            // on logged in
            if(mAuth.getCurrentUser()!=null) {
                userEmailTV.setText("Welcome " + mAuth.getCurrentUser().getDisplayName());
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

            // Move to Android Basic on button press
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
