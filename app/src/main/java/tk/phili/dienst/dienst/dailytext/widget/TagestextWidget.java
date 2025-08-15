package tk.phili.dienst.dienst.dailytext.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.dailytext.DailytextFragment;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;

/**
 * Implementation of App Widget functionality.
 */
public class TagestextWidget extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SharedPreferences sp = context.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        String day = sp.getString("dailytext_day", "--");
        String text = sp.getString("dailytext_text", "--");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.dailytext_widget_new);
        views.setTextViewText(R.id.ttw_day, day);
        views.setTextViewText(R.id.ttw_text, text);

        //views.setImageViewBitmap(R.id.ttw_day, getFontBitmap(context, day, Color.parseColor("#000000"), 24));
        //views.setImageViewBitmap(R.id.ttw_text, getFontBitmap(context, text, Color.parseColor("#000000"), 18));

        Intent intent = new Intent(context, WrapperActivity.class);
        intent.putExtra("shortcut_started", "Tagestext");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);

        views.setOnClickPendingIntent(R.id.tt_widget, pendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void errorAppWidget(Context context, AppWidgetManager appWidgetManager,
                               int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.dailytext_widget);
        views.setTextViewText(R.id.ttw_day, context.getString(R.string.widget_no_network_title));
        views.setTextViewText(R.id.ttw_text, context.getString(R.string.widget_no_network_text));


        // Setup update button to send an update request as a pending intent.
        Intent intentUpdate = new Intent(context, TagestextWidget.class);

        // The intent action must be an app widget update.
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // Include the widget ID to be updated as an intent extra.
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        // Wrap it all in a pending intent to send a broadcast.
        // Use the app widget ID as the request code (third argument) so that
        // each intent is unique.
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(context,
                appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

        // Assign the pending intent to the button onClick handler
        views.setOnClickPendingIntent(R.id.tt_widget, pendingUpdate);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

