package com.example.forgetMeNot.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class SecondAlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String food = intent.getStringExtra("Food");
        Boolean necessity = intent.getBooleanExtra("Necessity", true);
        SecondNotificationHelper notificationHelper = new SecondNotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(food, necessity);
        notificationHelper.getManager().notify(1, nb.build());
    }
}
