package com.example.forgetMeNot.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

public class Alarm {

    public static void cancelAlarm(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public static void setFirstAlarm(Context context, Date expiryDate, String food, boolean necessity, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("Food", food);
        intent.putExtra("Necessity", necessity);
        intent.putExtra("Alarm", 1);

        // Set first alarm 5 days before expiry date
        Calendar first = Calendar.getInstance();
        first.setTime(expiryDate);
        first.add(Calendar.DATE, -5);

        PendingIntent firstPendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, first.getTimeInMillis(), firstPendingIntent);
    }

    public static void setSecondAlarm(Context context, Date expiryDate, String food, boolean necessity, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("Food", food);
        intent.putExtra("Necessity", necessity);
        intent.putExtra("Alarm", 2);

        // Set second alarm on expiry date
        Calendar second = Calendar.getInstance();
        second.setTime(expiryDate);

        // requestCode for second alarm will be first * 2
        PendingIntent secondPendingIntent = PendingIntent.getBroadcast(context, requestCode * 2, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, second.getTimeInMillis(), secondPendingIntent);
    }

}
