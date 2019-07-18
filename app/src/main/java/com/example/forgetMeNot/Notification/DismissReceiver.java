package com.example.forgetMeNot.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

public class DismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String food = intent.getStringExtra("Food");
        long expiry = intent.getLongExtra("Expiry in ms", 0);
        Boolean necessity = intent.getBooleanExtra("Necessity", true);

        // Set Alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, SecondAlertReceiver.class);
        alarmIntent.putExtra("Food", food);
        alarmIntent.putExtra("Necessity", necessity);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, food.hashCode(), alarmIntent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, expiry, pendingIntent);
    }
}
