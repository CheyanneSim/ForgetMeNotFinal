package com.example.forgetMeNot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.forgetMeNot.Authentication.LoginFragment;
import com.example.forgetMeNot.Authentication.LoginRegisterFragment;
import com.example.forgetMeNot.Authentication.RegisterFragment;
import com.example.forgetMeNot.SharingData.GroupFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static TextView name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Handle drawer.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Display user's name and email in navigation view
        View navHeader = navigationView.getHeaderView(0);
        name = (TextView) navHeader.findViewById(R.id.name_textView);
        email = (TextView) navHeader.findViewById(R.id.email_textView);
        setNameAndEmail(mAuth);

        // choose which screen u want to show first.
        if (mAuth.getCurrentUser() != null) {
            displaySelectedScreen(R.id.nav_home);
        } else {
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, new LoginRegisterFragment());
            ft.commit();
        }


        //Firestore Setup
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        mAuth = FirebaseAuth.getInstance();

        // add auth change listener
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // if logged in, remove from nav bar items
                if(firebaseAuth.getCurrentUser()!=null) {

                    Menu navMenuLogIn = navigationView.getMenu();
                    navMenuLogIn.findItem(R.id.nav_login).setVisible(false);
                    navMenuLogIn.findItem(R.id.nav_register).setVisible(false);
                    navMenuLogIn.findItem(R.id.nav_logout).setVisible(true);
                    navMenuLogIn.findItem(R.id.nav_group).setVisible(true);
                } else {
                    // show items in nav bar
                    Menu navMenuLogIn = navigationView.getMenu();
                    navMenuLogIn.findItem(R.id.nav_login).setVisible(true);
                    navMenuLogIn.findItem(R.id.nav_register).setVisible(true);
                    navMenuLogIn.findItem(R.id.nav_logout).setVisible(false);
                    navMenuLogIn.findItem(R.id.nav_group).setVisible(false);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //displaying the selected screen
    private void displaySelectedScreen(int id) {

        // creating the fragment
        Fragment fragment = null;


        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_register) {
            fragment = new RegisterFragment();
        } else if (id == R.id.nav_login) {
            fragment = new LoginFragment();
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
            name.setText("Welcome");
            fragment = new LoginFragment();
        } else if (id == R.id.nav_group) {
            fragment = new GroupFragment();
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
    }

    public static void setNameAndEmail(FirebaseAuth mAuth) {
        if (mAuth.getCurrentUser() != null) {
            name.setText(mAuth.getCurrentUser().getDisplayName());
            email.setText(mAuth.getCurrentUser().getEmail());
        }
    }

    // Used after registration only
    public static void setNameAndEmail(String inputname, String inputemail) {
        name.setText(inputname);
        email.setText(inputemail);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        int id = item.getItemId();

        displaySelectedScreen(id);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
