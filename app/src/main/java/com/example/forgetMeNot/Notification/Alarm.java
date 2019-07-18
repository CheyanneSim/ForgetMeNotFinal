package com.example.forgetMeNot.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

public class Alarm {

    public static void cancelAlarm(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public static void setAlarm(Context context, Date expiryDate, String food, boolean necessity, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("Food", food);
        intent.putExtra("Necessity", necessity);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, expiryDate.getTime(), pendingIntent);
    }

}
