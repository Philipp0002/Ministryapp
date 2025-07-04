package tk.phili.dienst.dienst;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import tk.phili.dienst.dienst.calendar.CalendarWorker;
import tk.phili.dienst.dienst.settings.GDPRInfo;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.Shortcuts;


public class Splash extends Activity {

    boolean isUp = true;
    public SharedPreferences sp;
    static String s;

    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            setTheme(R.style.Splash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.splash);
        isUp = true;

        sp = getPreferences(Context.MODE_PRIVATE);

        createNotificationChannel();

        PeriodicWorkRequest.Builder builder =
                new PeriodicWorkRequest.Builder(CalendarWorker.class, 15,
                        TimeUnit.MINUTES);
        PeriodicWorkRequest periodicWorkRequest = builder.build();
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork(
                        "Calendar",
                        ExistingPeriodicWorkPolicy.KEEP,
                        periodicWorkRequest
                );

        //((TextView) findViewById(R.id.textView2)).setTypeface(Typeface.createFromAsset(getAssets(), "HammersmithOne-Regular.ttf"));

        s = getIntent().getStringExtra("Activity");

        try {
            Shortcuts.updateShortcuts(Splash.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        runDelayedHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUp = true;
        createNotificationChannel();
        runDelayedHandler();
    }

    public void runDelayedHandler() {
        if (handler != null) {
            return;
        }
        handler = new Handler();
        handler.postDelayed(() -> {
            if (isUp) {
                Intent mainIntent = new Intent(Splash.this, WrapperActivity.class);

                mainIntent.putExtra("shortcut_started", s);

                if (!sp.getBoolean("dsgvo_accept", false)) {
                    mainIntent = new Intent(Splash.this, GDPRInfo.class);
                }

                Splash.this.startActivity(mainIntent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                Splash.this.finish();
            } else {
                handler = null;
            }
        }, 500);
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelCalendar = new NotificationChannel("calendar", getString(R.string.title_calendar), NotificationManager.IMPORTANCE_HIGH);
            channelCalendar.setDescription(getString(R.string.title_calendar));
            NotificationChannel channelReportTimer = new NotificationChannel("reportTimer", getString(R.string.timer_report_title), NotificationManager.IMPORTANCE_DEFAULT);
            channelReportTimer.setDescription(getString(R.string.timer_report_title));
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannels(Arrays.asList(channelCalendar, channelReportTimer));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isUp = false;
    }


}
