package com.ankhrom.base.common;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

public class NotificationHelper { // TODO: 02/05/16 refactor

    public static void notify(Context context, Class activityClass, String extra_subject, int iconResource, int iconColorResource, int largeIconResource, String title, String text, int notificationId) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(iconResource)
                .setContentTitle(title)
                .setContentText(text)
                .setColor(ContextCompat.getColor(context, iconColorResource))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        if (largeIconResource > 0) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIconResource));
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, activityClass);
        if (extra_subject != null) {
            resultIntent.putExtra(Intent.EXTRA_SUBJECT, extra_subject);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(activityClass);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationId, builder.build());
    }

    public static void cancel(Context context, int notificationId) {

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notificationId);
    }

    public static void cancelAll(Context context) {

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }
}
