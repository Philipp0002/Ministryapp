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
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class Splash extends Activity {

    boolean isUp = true;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;
    static String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            setTheme(R.style.Splash);
        }catch(Exception e){
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        isUp = true;

        sp = getPreferences(Context.MODE_PRIVATE);
        editor = sp.edit();

        createNotificationChannel();

        PeriodicWorkRequest.Builder builder =
                new PeriodicWorkRequest.Builder(KalenderWorker.class, 15,
                        TimeUnit.MINUTES);
        PeriodicWorkRequest periodicWorkRequest = builder.build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("Calendar",  ExistingPeriodicWorkPolicy.KEEP,periodicWorkRequest);

        ((TextView)findViewById(R.id.textView2)).setTypeface(Typeface.createFromAsset(getAssets(), "HammersmithOne-Regular.ttf"));

        s = getIntent().getStringExtra("Activity");

        Shortcuts.updateShortcuts(Splash.this);

        runDelayedHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUp = true;
        createNotificationChannel();
        runDelayedHandler();
    }

    public void runDelayedHandler(){
        new Handler().postDelayed(() -> {
            if(isUp) {
                Intent mainIntent = null;

                if(s == null || s.equals("MainActivity")) {
                    mainIntent = new Intent(Splash.this, MainActivity.class);
                }else if(s.equals("Notizen")) {
                    mainIntent = new Intent(Splash.this, Notizen.class);
                }else if(s.equals("Empfehlungen")) {
                    mainIntent = new Intent(Splash.this, Empfehlungen.class);
                }else if(s.equals("Videos")) {
                    mainIntent = new Intent(Splash.this, VideoNew.class);
                }else if(s.equals("Tagestext")) {
                    mainIntent = new Intent(Splash.this, Tagestext.class);
                }else if(s.equals("Kalender")) {
                    mainIntent = new Intent(Splash.this, Kalender.class);
                }

                if(!sp.getBoolean("dsgvo_accept", false)){
                    mainIntent = new Intent(Splash.this, DSGVOInfo.class);
                }

                Splash.this.startActivity(mainIntent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                Splash.this.finish();
            }
        }, 500);
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.title_section9);
            String description = getString(R.string.title_section9);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("calendar", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isUp = false;
    }


}
