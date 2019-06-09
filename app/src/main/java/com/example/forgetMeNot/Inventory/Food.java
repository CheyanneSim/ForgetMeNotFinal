package com.example.forgetMeNot.Inventory;

import com.example.forgetMeNot.Authentication.UserDetails;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Food {

    public static final String itemKey = "Food";
    public static final String expiryKey = "Expiry Date";
    public static final String availabilityKey = "Availability";
    protected String food;
    protected String expiry;
    protected boolean availability;

    public Food() {}

    public Food(String food, String expiry, boolean availability) {
        this.food = food;
        this.expiry = expiry;
        this.availability = availability;
    }

    public String getFood() {
        return food;
    }

    public String getExpiry() {
        return expiry;
    }

    public boolean getAvailability() {
        return availability;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public void createEntry(CollectionReference collectionReference) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> data = new HashMap<>();

        data.put(itemKey, food);
        data.put(expiryKey, expiry);
        data.put(availabilityKey, availability);
        collectionReference.document(food).set(data);
    }
}
