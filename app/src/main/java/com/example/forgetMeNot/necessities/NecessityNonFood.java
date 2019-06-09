package com.example.forgetMeNot.necessities;

import com.google.firebase.firestore.CollectionReference;

import java.util.HashMap;
import java.util.Map;

public class NecessityNonFood implements Necessity {

    private String item;
    private boolean availability;

    public NecessityNonFood() {}

    public NecessityNonFood(String item, boolean availability) {
        this.item = item;
        this.availability = availability;
    }

    public String getItem() {
        return item;
    }

    public boolean getAvailability() {
        return availability;
    }

    @Override
    public void createEntry(CollectionReference collectionReference) {

        Map<String,Object> data = new HashMap<>();

        data.put(itemKey, item);
        data.put(availabilityKey, availability);
        collectionReference.document(item).set(data);
    }
}
