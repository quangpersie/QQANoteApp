package com.example.noteapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    Context mContext;
    String channel = MyApplication.CHANNEL_ID;
    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Toast.makeText(context, "Received", Toast.LENGTH_SHORT).show();
        Log.e("MS","ReceivedNoti");

        String title = intent.getStringExtra("title_note");
        String desc = intent.getStringExtra("desc_note");
        String name_sound = intent.getStringExtra("name_sound");

        Intent resultIntent = new Intent(context,CreateNoteActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        switch (name_sound) {
            case "MTP":
                channel = MyApplication.CHANNEL_ID1;
                uri = Uri.parse("android.resource://"+mContext.getPackageName()+"/"+R.raw.sound_notify_1);
                break;
            case "MCK":
                channel = MyApplication.CHANNEL_ID2;
                uri = Uri.parse("android.resource://"+mContext.getPackageName()+"/"+R.raw.sound_notify_2);
                break;
            case "TNS":
                channel = MyApplication.CHANNEL_ID3;
                uri = Uri.parse("android.resource://"+mContext.getPackageName()+"/"+R.raw.sound_notify_3);
                break;
            default:
                channel = MyApplication.CHANNEL_ID;
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,0,mainIntent,0);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(context, channel)
                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(desc))
                .setColor(context.getResources().getColor(R.color.ic_launcher_background))
                .setSound(uri)
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(getNotificationId(), notification);
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }
}
