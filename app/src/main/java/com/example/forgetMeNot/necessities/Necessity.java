package com.example.forgetMeNot.necessities;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public interface Necessity {

    public static final String itemKey = "Necessity";
    public static final String availabilityKey = "Availability";
    public static final String isFoodKey = "isFood";

    public void createEntry(CollectionReference collectionReference);
    //public void deleteEntry();
}
