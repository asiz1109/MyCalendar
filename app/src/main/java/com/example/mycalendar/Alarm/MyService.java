package com.example.mycalendar.Alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mycalendar.MainActivity;
import com.example.mycalendar.R;

import java.util.Objects;

public class MyService extends JobIntentService {

    public static final String CHANNEL_ID = "MyServiceChannel";
    public static final int NOTIFICATION_ID = 1;


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final String event = Objects.requireNonNull(intent).getStringExtra("event");
        final String time = Objects.requireNonNull(intent).getStringExtra("time");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MyService Channel", NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(getSystemService(NotificationManager.class)).createNotificationChannel(channel);
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

        stopSelf();
    }

}
