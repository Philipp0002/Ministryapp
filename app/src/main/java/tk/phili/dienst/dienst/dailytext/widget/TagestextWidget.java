package tk.phili.dienst.dienst.dailytext.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;
import android.widget.RemoteViews;


import androidx.core.content.res.ResourcesCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.dailytext.Tagestext;
import tk.phili.dienst.dienst.dailytext.TagestextJSONAsyncFetcher;

/**
 * Implementation of App Widget functionality.
 */
public class TagestextWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String day, String text) {



        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tagestext_widget_new);
        views.setTextViewText(R.id.ttw_day, day);
        views.setTextViewText(R.id.ttw_text, text);

        //views.setImageViewBitmap(R.id.ttw_day, getFontBitmap(context, day, Color.parseColor("#000000"), 24));
        //views.setImageViewBitmap(R.id.ttw_text, getFontBitmap(context, text, Color.parseColor("#000000"), 18));

        Intent intent = new Intent(context, Tagestext.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);

        views.setOnClickPendingIntent(R.id.tt_widget, pendingIntent);



        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void errorAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tagestext_widget);
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


    public static Bitmap getFontBitmap(Context context, String text, int color, float fontSizeSP) {
        int fontSizePX = convertDiptoPix(context, fontSizeSP);
        int pad = (fontSizePX / 9);
        Paint paint = new Paint();
        Typeface typeface = ResourcesCompat.getFont(context, R.font.rubik);
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setColor(color);
        paint.setTextSize(fontSizePX);

        int textWidth = (int) (paint.measureText(text) + pad * 2);
        int height = (int) (fontSizePX / 0.75);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        float xOriginal = pad;
        canvas.drawText(text, xOriginal, fontSizePX, paint);
        return bitmap;
    }

    public static int convertDiptoPix(Context context, float dip) {
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return value;
    }

    public boolean isConnectedtoNet(Context c){
        ConnectivityManager connectivityManager = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }else {
            return false;
        }

    }

    public static String convertStringToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {



        if(isConnectedtoNet(context)) {
            int d = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int m = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int y = Calendar.getInstance().get(Calendar.YEAR);

            SharedPreferences sp = context.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
            String lang = sp.getString("tt_locale", Locale.getDefault().getLanguage());

            final TagestextJSONAsyncFetcher asyncFetcher = new TagestextJSONAsyncFetcher();
            asyncFetcher.year = y;
            asyncFetcher.month = m;
            asyncFetcher.day = d;
            asyncFetcher.lang = lang;

            asyncFetcher.futurerun = new Runnable() {
                @Override
                public void run() {
                    JSONObject obj = asyncFetcher.response;
                    if (obj != null) {
                        try {
                            String day = obj.getString("date");
                            String text = obj.getString("text");
                            String bible = obj.getString("bible");

                            // There may be multiple widgets active, so update all of them
                            for (int appWidgetId : appWidgetIds) {
                                updateAppWidget(context, appWidgetManager, appWidgetId, day, text);
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        for (int appWidgetId : appWidgetIds) {
                            //errorAppWidget(context, appWidgetManager, appWidgetId);
                        }
                    }
                }
            };
            asyncFetcher.execute();
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

