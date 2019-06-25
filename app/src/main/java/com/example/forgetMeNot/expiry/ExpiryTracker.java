package com.example.forgetMeNot.expiry;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.example.forgetMeNot.R;

// TODO
// Expandable list view: http://androidtechpoint.blogspot.com/2017/01/android-expandable-listview.html
// sort according to expiry
// allow user to update expiry
// notification stuff

public class ExpiryTracker extends AppCompatActivity {

    // For expandable list view
    private static ExpandableListView necessitieslv;
    private static ExpandableListView nonEssentiallv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expiry);

        displaySelectedScreen(new NecessitiesExpiryFragment());

        // for action bar at the top of the screen
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.Necessities_menu:
                        fragment = new NecessitiesExpiryFragment();
                        break;
                    case R.id.NonEssentials_menu:
                        fragment = new NonEssentialsExpiryFragment();
                        break;
                }
                return displaySelectedScreen(fragment);
            }
        });
    }

    // helper method to change current display
    private boolean displaySelectedScreen(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.expiry_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    // Set back button to finish activity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
