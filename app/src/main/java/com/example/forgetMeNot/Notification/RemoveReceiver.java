package com.example.forgetMeNot.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.forgetMeNot.SharingData.GroupFragment.GROUP;
import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class RemoveReceiver extends BroadcastReceiver {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference necessitiesCollectionRef;
    private CollectionReference nonEssentialCollectionRef;
    public String group;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        group = sharedPreferences.getString(GROUP, "");
        String food = intent.getStringExtra("Food");
        boolean necessity = intent.getBooleanExtra("Necessity", true);
        int alarmNo = intent.getIntExtra("Alarm", 0);
        if (necessity) {
            necessitiesCollectionRef = db.collection("Groups").document(group).collection("Necessities");
            necessitiesCollectionRef.document(food).update("Availability", false);
            necessitiesCollectionRef.document(food).update("Expiry Date", null);
        } else {
            nonEssentialCollectionRef = db.collection("Groups").document(group).collection("Non-essentials");
            nonEssentialCollectionRef.document(food).delete();
        }

        // Cancel 2nd alarm if it is the first alarm
        if (alarmNo == 1) {
            Log.d("RemoveReceiver", "Second alarm cancelled");
            Alarm.cancelAlarm(context, food.hashCode() * 2);
        }

        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }
}
