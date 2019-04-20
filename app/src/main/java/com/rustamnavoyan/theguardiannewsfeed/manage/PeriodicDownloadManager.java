package com.rustamnavoyan.theguardiannewsfeed.manage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class PeriodicDownloadManager {
    private static final int INTERVAL = 30 * 1000; // 30 seconds

    public static void schedule(Context context) {
        Intent intent = new Intent(context, ArticleDownloadReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + INTERVAL, INTERVAL, pendingIntent);
    }

    public static void cancel(Context context) {
        Intent intent = new Intent(context, ArticleDownloadReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
