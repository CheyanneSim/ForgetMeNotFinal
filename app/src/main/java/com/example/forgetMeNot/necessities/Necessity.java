package com.example.forgetMeNot.necessities;

import com.google.firebase.firestore.CollectionReference;

import java.util.Date;


public interface Necessity {

    public static final String itemKey = "Necessity";
    public static final String availabilityKey = "Availability";
    public static final String expiryKey = "Expiry Date";


    public String getName();
    public Date getExpiry();
    public boolean getAvailability();

    public void createEntry(CollectionReference collectionReference);
    //public void deleteEntry();
}
