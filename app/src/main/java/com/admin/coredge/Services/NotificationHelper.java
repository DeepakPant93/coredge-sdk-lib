package com.admin.coredge.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {

    private static final String EDMT_CHANNEL_ID="com.example.safna.notifier1.EDMTDEV";
    private static final String EDMT_CHANNEL_NAME="EDMTDEV Channel";
    private NotificationManager manager;

    public NotificationHelper(Context base)
    {
        super(base);
        createChannels();
    }
    private void createChannels()
    {
//        NotificationChannel edmtChannel= new NotificationChannel(EDMT_CHANNEL_ID,EDMT_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
//        edmtChannel.enableLights(true);
//        edmtChannel.enableVibration(true);
//        edmtChannel.setLightColor(Color.GREEN);
//        edmtChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//
//        getManager().createNotificationChannel(edmtChannel);

    }
    public NotificationManager getManager()
    {
        if (manager==null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;

    }
    public NotificationCompat.Builder getEDMTChannelNotification(String title, String body)
    {
        return new NotificationCompat.Builder(getApplicationContext(),EDMT_CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
               // .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true);
    }
}
