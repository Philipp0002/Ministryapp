package tk.phili.dienst.dienst;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class Splash extends AppCompatActivity {

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

        ((TextView)findViewById(R.id.textView2)).setTypeface(Typeface.createFromAsset(getAssets(), "HammersmithOne-Regular.ttf"));

        s = getIntent().getStringExtra("Activity");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isUp) {
                    Intent mainIntent = null;

                    if(s == null || s.equals("MainActivity")) {
                        mainIntent = new Intent(Splash.this, MainActivity.class);
                    }else if(s.equals("Gebiete")) {
                        mainIntent = new Intent(Splash.this, Gebiete.class);
                    }else if(s.equals("Notizen")) {
                        mainIntent = new Intent(Splash.this, Notizen.class);
                    }else if(s.equals("Empfehlungen")) {
                        mainIntent = new Intent(Splash.this, Empfehlungen.class);
                    }else if(s.equals("Videos")) {
                        mainIntent = new Intent(Splash.this, VideoNew.class);
                    }

                    if(!sp.getBoolean("dsgvo_accept", false)){
                        mainIntent = new Intent(Splash.this, DSGVOInfo.class);
                    }

                    Splash.this.startActivity(mainIntent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    Splash.this.finish();
                }
            }
        }, 500);//3
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUp = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isUp) {
                    Intent mainIntent = null;

                    if(s == null || s.equals("MainActivity")) {
                        mainIntent = new Intent(Splash.this, MainActivity.class);
                    }else if(s.equals("Gebiete")) {
                        mainIntent = new Intent(Splash.this, Gebiete.class);
                    }else if(s.equals("Notizen")) {
                        mainIntent = new Intent(Splash.this, Notizen.class);
                    }else if(s.equals("Empfehlungen")) {
                        mainIntent = new Intent(Splash.this, Empfehlungen.class);
                    }else if(s.equals("Videos")) {
                        mainIntent = new Intent(Splash.this, VideoNew.class);
                    }

                    if(!sp.getBoolean("dsgvo_accept", false)){
                        mainIntent = new Intent(Splash.this, DSGVOInfo.class);
                    }

                    Splash.this.startActivity(mainIntent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    Splash.this.finish();
                }
            }
        }, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isUp = false;
    }


}
