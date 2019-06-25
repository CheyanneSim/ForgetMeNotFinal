package com.example.forgetMeNot.necessities;

import com.google.firebase.firestore.CollectionReference;


public interface Necessity {

    public static final String itemKey = "Necessity";
    public static final String availabilityKey = "Availability";
    public static final String expiryKey = "Expiry Date";


    public String getName();
    public String getExpiry();
    public boolean getAvailability();

    public void createEntry(CollectionReference collectionReference);
    //public void deleteEntry();
}
