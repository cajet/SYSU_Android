package com.example.cajet.text_contact.note;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.cajet.text_contact.R;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private MediaPlayer mp = null;

    public MediaPlayer getMp() {
        return mp;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals("cancel")) {
                context.stopService(new Intent(context, MusicService.class));
                Intent intent1 = new Intent(context, Main.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
            if (action.equals("myAlarmAction")) {
                context.startService(new Intent(context, MusicService.class));
                Toast.makeText(context, "It's time!!!", Toast.LENGTH_LONG).show();
                NotificationManager manager =
                        (NotificationManager) context.getSystemService(Context.
                                NOTIFICATION_SERVICE
                        );
                Bitmap bitmap = BitmapFactory.
                        decodeResource
                                (context.getResources(), R.mipmap.check);
                Notification.Builder builder = new Notification.Builder(context);
                builder.setContentTitle("It's time!")
                        .setContentText("It's time!!!")
                        .setTicker("It's time!!!")
                        .setLargeIcon(bitmap)
                        .setSmallIcon(R.mipmap.check)
                        .setAutoCancel(true)
                        .setContentIntent(PendingIntent.getBroadcast(context, 0,
                                new Intent("cancel"), 0));

                Notification notify = builder.build();
                manager.notify(0, notify);
            }
            if (action.equals("myWidgetAction")) {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget_provider);
                remoteViews.setTextViewText(R.id.widgetDate, intent.getStringExtra("time"));
                remoteViews.setTextViewText(R.id.widgetTheme, intent.getStringExtra("theme"));
                remoteViews.setTextViewText(R.id.widgetText, intent.getStringExtra("text"));
                remoteViews.setTextViewText(R.id.widgetDeadTime, intent.getStringExtra("deadline"));
                AppWidgetManager.getInstance(context).updateAppWidget(
                        new ComponentName(context.getApplicationContext(),
                                MyWidgetProvider.class), remoteViews);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
