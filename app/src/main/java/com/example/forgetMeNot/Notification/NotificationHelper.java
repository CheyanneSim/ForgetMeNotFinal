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

    public NotificationCompat.Builder getChannelNotification(String food, Boolean necessity) {

        // When user clicks the notification, open the app to the inventory page
        Intent intent = new Intent(getApplicationContext(), MyInventory.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        // Action: if expired item is no longer available
        Intent unavailable = new Intent(getApplicationContext(), UnavailableReceiver.class);
        unavailable.putExtra("Necessity", necessity);
        unavailable.putExtra("Food", food);
        PendingIntent actionIntent = PendingIntent.getBroadcast(getApplicationContext(), food.hashCode(), unavailable, 0);

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Forget Me Not!")
                .setContentText("Your " + food + " has expired!")
                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                .addAction(R.mipmap.ic_launcher, "It's gone!", actionIntent)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);
    }
}
