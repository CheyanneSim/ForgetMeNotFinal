package com.example.forgetMeNot.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.forgetMeNot.SharingData.GroupFragment.GROUP;
import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class UnavailableReceiver extends BroadcastReceiver {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference necessitiesCollectionRef;
    private CollectionReference nonEssentialCollectionRef;
    public String group;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        group = sharedPreferences.getString(GROUP, "");
        String food = intent.getStringExtra("Food");
        Boolean necessity = intent.getBooleanExtra("Necessity", true);
        if (necessity) {
            necessitiesCollectionRef = db.collection("Groups").document(group).collection("Necessities");
            necessitiesCollectionRef.document(food).update("Availability", false);
            necessitiesCollectionRef.document(food).update("Expiry Date", null);
        } else {
            nonEssentialCollectionRef = db.collection("Groups").document(group).collection("Non-essentials");
            nonEssentialCollectionRef.document(food).delete();
        }
        //TODO not working - dismiss notification
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }
}
