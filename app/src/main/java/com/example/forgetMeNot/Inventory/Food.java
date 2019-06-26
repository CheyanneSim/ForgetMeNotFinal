package com.example.forgetMeNot.Inventory;

import com.google.firebase.firestore.CollectionReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Food {

    public static final String itemKey = "Food";
    public static final String expiryKey = "Expiry Date";
    public static final String availabilityKey = "Availability";
    protected String food;
    protected Date expiry;
    protected boolean availability;

    public Food() {}

    public Food(String food, Date expiry, boolean availability) {
        this.food = food;
        this.expiry = expiry;
        this.availability = availability;
    }

    public String getFood() {
        return food;
    }

    public Date getExpiry() {
        return expiry;
    }

    public boolean getAvailability() {
        return availability;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public void createEntry(CollectionReference collectionReference) {
        Map<String,Object> data = new HashMap<>();

        data.put(itemKey, food);
        data.put(expiryKey, expiry);
        data.put(availabilityKey, availability);
        collectionReference.document(food).set(data);
    }
}
