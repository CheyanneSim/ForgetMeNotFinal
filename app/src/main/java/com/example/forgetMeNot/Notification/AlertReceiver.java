package com.example.forgetMeNot.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String food = intent.getStringExtra("Food");
        Log.d("Alert Receiver", food);
        Boolean necessity = intent.getBooleanExtra("Necessity", true);
        int alarmNo = intent.getIntExtra("Alarm", 0);
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(food, necessity, alarmNo);
        notificationHelper.getManager().notify(food.hashCode(), nb.build());
    }
}
