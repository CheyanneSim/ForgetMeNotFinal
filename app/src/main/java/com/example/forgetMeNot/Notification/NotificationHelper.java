package com.example.forgetMeNot.Notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.forgetMeNot.Inventory.MyInventory;
import com.example.forgetMeNot.R;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";

    private NotificationManager mManager;

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
                .setContentTitle("Forget Me Not!")
                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                .addAction(R.mipmap.ic_launcher, "Food cleared!", removeIntent)
                .addAction(R.mipmap.ic_launcher, "Purchase!", purchaseIntent)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        if (alarmNo == 1) {
            notif.setContentText("Your " + food + " is expiring in less than 6 days!");
        } else {
            notif.setContentText("Your " + food + " has expired!");
        }

        return notif;
    }
}
