package com.example.forgetMeNot.Notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.forgetMeNot.Inventory.MyInventory;
import com.example.forgetMeNot.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.forgetMeNot.SharingData.GroupFragment.GROUP;
import static com.example.forgetMeNot.SharingData.GroupFragment.SHARED_PREFS;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";

    private NotificationManager mManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference extraShoppingListCollection;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(String food, Boolean necessity, int alarmNo) {

        // When user clicks the notification, open the app to the inventory page
        Intent intent = new Intent(getApplicationContext(), MyInventory.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        // Action: if expired item is no longer available
        Intent remove = new Intent(getApplicationContext(), RemoveReceiver.class);
        remove.putExtra("Necessity", necessity);
        remove.putExtra("Food", food);
        remove.putExtra("Alarm", alarmNo);
        PendingIntent removeIntent = PendingIntent.getBroadcast(getApplicationContext(), food.hashCode(), remove, PendingIntent.FLAG_UPDATE_CURRENT);

        // Action: Purchase
        Intent purchase = new Intent(getApplicationContext(), PurchaseReceiver.class);
        purchase.putExtra("Necessity", necessity);
        purchase.putExtra("Food", food);
        purchase.putExtra("Alarm", alarmNo);
        PendingIntent purchaseIntent = PendingIntent.getBroadcast(getApplicationContext(), food.hashCode(), purchase, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(food)
                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                .addAction(R.mipmap.ic_launcher, "Food cleared!", removeIntent)
                .addAction(R.mipmap.ic_launcher, "Purchase!", purchaseIntent)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        if (alarmNo == 1) {
            notif.setContentText("Expiring in less than 6 days!");
        } else {
            notif.setContentText("Expired! Remember to discard!");
            // If it is necessity, add to shopping list.
            if (necessity) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                String group = sharedPreferences.getString(GROUP, "");
                extraShoppingListCollection = db.collection("Groups").document(group).collection("Shopping List");
                Map<String, Object> data = new HashMap<>();
                data.put("Item", food);
                // If it's not food, it will be part of Necessities
                data.put("Is Food", true);
                extraShoppingListCollection.document(food).set(data);
            }
        }

        return notif;
    }
}
