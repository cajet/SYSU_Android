package com.example.cajet.text_contact.note;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.cajet.text_contact.R;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                new Intent("cancel"), 0);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.my_widget_provider);
        remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}

