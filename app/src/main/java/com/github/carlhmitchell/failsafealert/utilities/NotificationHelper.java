package com.github.carlhmitchell.failsafealert.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.github.carlhmitchell.failsafealert.MainActivity;
import com.github.carlhmitchell.failsafealert.R;

import java.util.Objects;

import static com.github.carlhmitchell.failsafealert.utilities.AppConstants.NOTIFICATION_CHANNEL_ID;

public class NotificationHelper {
    private final Context notificationHelperContext;

    public NotificationHelper(Context context) {
        notificationHelperContext = context;
    }

    public void sendNotification(String title, String text) {
        // Get an instance of NotificationManager
        Intent intent = new Intent(notificationHelperContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(notificationHelperContext, 0, intent, 0);
        NotificationCompat.Builder mBuilder;
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder = new NotificationCompat.Builder(notificationHelperContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setSound(uri);

        // Get an instance of the NotificationManager service
        NotificationManager mNotificationManager = (NotificationManager) notificationHelperContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notifications can be updated, instead of just replaced. They need an ID for this.
        Objects.requireNonNull(mNotificationManager).notify(2, mBuilder.build());
    }
}
