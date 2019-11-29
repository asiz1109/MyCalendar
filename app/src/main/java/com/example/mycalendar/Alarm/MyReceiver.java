package com.example.mycalendar.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String event = intent.getStringExtra("event");
        String time = intent.getStringExtra("time");
        context.startService(new Intent(context, MyService.class).putExtra("event", event).putExtra("time", time));
    }
}
