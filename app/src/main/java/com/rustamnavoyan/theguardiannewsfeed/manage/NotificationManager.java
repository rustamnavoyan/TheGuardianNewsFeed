package com.rustamnavoyan.theguardiannewsfeed.manage;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.rustamnavoyan.theguardiannewsfeed.MainActivity;
import com.rustamnavoyan.theguardiannewsfeed.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationManager {
    private static final int NEWS_NOTIFICATION_ID = 2;
    public static final String NEWS_NOTIFICATION_CHANNEL_ID = "NEWS_NOTIFICATION_CHANNEL_ID";

    public static void showNewsFeedNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NEWS_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setContentTitle(context.getString(R.string.completed_notification_title));

        NotificationManagerCompat.from(context).notify(NEWS_NOTIFICATION_ID, builder.build());
    }

    public static void clearNotifications(Context context) {
        android.app.NotificationManager manager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }
}
