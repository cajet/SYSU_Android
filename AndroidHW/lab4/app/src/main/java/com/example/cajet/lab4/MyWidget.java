package com.example.cajet.lab4;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Toast.makeText(context, "hhh", Toast.LENGTH_SHORT).show();
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pintent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        rv.setOnClickPendingIntent(R.id.wid_image, pintent);
        ComponentName me = new ComponentName(context, MyWidget.class);
        appWidgetManager.updateAppWidget(me, rv);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        Bundle bundle = intent.getExtras();

        if (intent.getAction().equals("com.example.cajet.lab4.staticreceiver")) {
            HashMap map = (HashMap) intent.getSerializableExtra("fruit");
            rv.setImageViewResource(R.id.wid_image, (int) map.get("image_id"));
            rv.setTextViewText(R.id.wid_name, map.get("name").toString());
            //Toast.makeText(context, "hahaha", Toast.LENGTH_SHORT).show();
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName me = new ComponentName(context, MyWidget.class);
            appWidgetManager.updateAppWidget(me, rv);
        }
    }
}
