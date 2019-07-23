package com.example.forgetMeNot.Tutorial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.forgetMeNot.MainActivity;
import com.example.forgetMeNot.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class Tutorial extends AppCompatActivity {

    private ViewPager screenPager;
    TutorialViewPagerAdapter tutorialViewPagerAdapter;
    TabLayout tabIndicator;
    Button next;
    Button getStarted;
    int position = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // If user already done tutorial, go to main activity
        if (tutorialDone()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_tutorial);

        // hide the action bar
        getSupportActionBar().hide();

        tabIndicator = findViewById(R.id.tab_indicator);
        next = findViewById(R.id.next_button);
        getStarted = findViewById(R.id.start_button);


        // fill list screen
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Sharing Data",
                "You need to be in a group to begin using Forget Me Not, so that the data is shared amongst everyone in the group. \n Note that the group name is case sensitive.",
                R.drawable.group));
        mList.add(new ScreenItem("My Necessities",
                "Necessities are things that you need a constant supply of, that is, things that you need to replenish when you run out of them. \n When you run out of them, or if they have expired, they will be added to My Shopping List automatically.",
                R.drawable.groceries_icon));
        mList.add(new ScreenItem("My Inventory",
                "Check out what you have available at your finger tips in My Inventory. Toggle on the purchase switch to add those items to My Shopping List before they even run out.\n You can also add non-essential food items, like snacks, into My Inventory.",
                R.drawable.snacks_icon));
        mList.add(new ScreenItem("My Shopping List",
                "Keep track of what you have purchased and they will be added to My Inventory. \n Feel free to add more things into My Shopping List.",
                R.drawable.shopping_icon));
        mList.add(new ScreenItem("Expiry Date Tracker",
                "You will get your first reminder 5 days before the expiry date. Click on 'Food Cleared!', and it will be removed from My Inventory. Click on 'Purchase!' and it will be removed from my Inventory and included in My Shopping List. You will get a second reminder on the expiry day itself.",
                R.drawable.expiry_icon));

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        tutorialViewPagerAdapter = new TutorialViewPagerAdapter(this, mList);
        screenPager.setAdapter(tutorialViewPagerAdapter);

        // setup tablayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }

                if (position == mList.size()) {
                    loadLastScreen();
                }
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size() - 1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                // So that user dont have to go through tutorial again
                saveSharedPref();
                finish();
            }
        });
    }

    private boolean tutorialDone() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Boolean isTutorialDone = preferences.getBoolean("Tutorial Done", false);
        return isTutorialDone;
    }


    private void saveSharedPref() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("Tutorial Done", true);
        editor.commit();
    }

    private void loadLastScreen() {
        next.setVisibility(View.INVISIBLE);
        getStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
    }
}
