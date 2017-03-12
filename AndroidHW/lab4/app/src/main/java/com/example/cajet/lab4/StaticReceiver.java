package com.example.cajet.lab4;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.HashMap;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by cajet on 2016/10/16.
 */

public class StaticReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        HashMap map= (HashMap) intent.getSerializableExtra("fruit");
        //Toast.makeText(context, map.get("image_id").toString()+ map.get("name").toString(), Toast.LENGTH_SHORT).show();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        Notification.Builder builder= new Notification.Builder(context);
        builder.setContentTitle("静态广播")
                .setContentText(map.get("name").toString())
                .setSmallIcon((int)map.get("image_id"))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), (int)map.get("image_id")))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }
}
