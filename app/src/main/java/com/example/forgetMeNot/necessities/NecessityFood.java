package com.example.forgetMeNot.necessities;

import com.example.forgetMeNot.Inventory.Food;
import com.google.firebase.firestore.CollectionReference;

import java.util.HashMap;
import java.util.Map;

public class NecessityFood extends Food implements Necessity {

    public static final String itemKey = "Necessity";

    public NecessityFood() {}

    public NecessityFood(String food, String expiry, boolean availability) {
        super(food, expiry, availability);
    }

    @Override
    public String getName() {
        return super.getFood();
    }

    @Override
    public String getExpiry() {
        return super.getExpiry();
    }

    @Override
    public boolean getAvailability() {
        return super.getAvailability();
    }

    @Override
    public void createEntry(CollectionReference collectionReference) {
        Map<String,Object> data = new HashMap<>();

        data.put(itemKey, food);
        data.put(Necessity.expiryKey, expiry);
        data.put(Necessity.availabilityKey, availability);
        collectionReference.document(food).set(data);
    }
}
