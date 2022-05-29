package com.example.noteapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static final String CHANNEL_ID1 = "CHANNEL_1";
    public static final String CHANNEL_ID2 = "CHANNEL_2";
    public static final String CHANNEL_ID3 = "CHANNEL_3";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage((AudioAttributes.USAGE_NOTIFICATION))
                    .build();

            //DEFAULT
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(uri, attributes);

            //SOUND1
            Uri uri1 = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sound_notify_1);
            CharSequence name1 = getString(R.string.channel_name);
            String description1 = getString(R.string.channel_description);
            int importance1 = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID1, name1, importance1);
            channel1.setDescription(description1);
            channel1.setSound(uri1, attributes);

            //SOUND2
            Uri uri2 = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sound_notify_2);
            CharSequence name2 = getString(R.string.channel_name);
            String description2 = getString(R.string.channel_description);
            int importance2 = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID2, name2, importance2);
            channel2.setDescription(description2);
            channel2.setSound(uri2, attributes);

            //SOUND3
            Uri uri3 = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sound_notify_3);
            CharSequence name3 = getString(R.string.channel_name);
            String description3 = getString(R.string.channel_description);
            int importance3 = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel3 = new NotificationChannel(CHANNEL_ID3, name3, importance3);
            channel3.setDescription(description3);
            channel3.setSound(uri3, attributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                notificationManager.createNotificationChannel(channel1);
                notificationManager.createNotificationChannel(channel2);
                notificationManager.createNotificationChannel(channel3);
            }
        }
    }
}