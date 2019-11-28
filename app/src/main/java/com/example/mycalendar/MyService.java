package com.example.mycalendar;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyService extends IntentService {

    public static final String CHANNEL_ID = "MyServiceChannel";
    public static final int NOTIFICATION_ID = 1;


    public MyService() {
        super("MyCalendar");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String event = intent.getStringExtra("event");
        String time = intent.getStringExtra("time");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MyService Channel", NotificationManager.IMPORTANCE_DEFAULT);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(event)
                .setContentText(time)
                .setSmallIcon(R.drawable.ic_alarm_24dp)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notification);
//      задача (будильник с событием и временем начала)
    }
}
