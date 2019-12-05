package com.example.mycalendar;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Objects;

public class MyApp extends Application {

    public static final String ALARM = "com.example.mycalendar.ALARM";
    public static final String CHANNEL_ID = "MyServiceChannel";
    private NotificationChannel channel;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, "MyService Channel", NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(getSystemService(NotificationManager.class)).createNotificationChannel(channel);
        }
    }
}
