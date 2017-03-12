package com.example.cajet.lab4;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by cajet on 2016/10/16.
 */

public class DynamicReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle= intent.getExtras();
        //Toast.makeText(context, bundle.get("send_word").toString(), Toast.LENGTH_SHORT).show();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        Notification.Builder builder= new Notification.Builder(context);
        builder.setContentTitle("动态广播")
                .setContentText(bundle.get("send_word").toString())
                .setSmallIcon(R.mipmap.dynamic)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.dynamic))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

        if (intent.getAction().equals("com.example.cajet.lab4.dynamicreceiver")) {
            RemoteViews rv= new RemoteViews(context.getPackageName(), R.layout.my_widget);
            rv.setImageViewResource(R.id.wid_image, R.mipmap.dynamic);
            rv.setTextViewText(R.id.wid_name, bundle.get("send_word").toString());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName me = new ComponentName(context, MyWidget.class);
            appWidgetManager.updateAppWidget(me, rv);
        }
    }
}
