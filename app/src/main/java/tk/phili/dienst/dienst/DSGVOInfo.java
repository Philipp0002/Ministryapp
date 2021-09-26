package tk.phili.dienst.dienst;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import java.util.Locale;

public class DSGVOInfo extends AppCompatActivity {

    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsgvoinfo);

        boolean b = getIntent().getBooleanExtra("hastoaccept", true);

        sp = getSharedPreferences("Splash", MODE_PRIVATE);
        editor = sp.edit();

        if(!b){
            findViewById(R.id.bottombar).setVisibility(View.GONE);
        }

        WebView web = findViewById(R.id.webView);
        if(Locale.getDefault().getLanguage().equalsIgnoreCase("de")) {
            web.loadUrl("https://ministryapp.de/de/gdpr.html");
        }else{
            web.loadUrl("https://ministryapp.de/en/gdpr.html");
        }

        findViewById(R.id.accept_dsgvo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean("dsgvo_accept", true);
                editor.commit();

                Intent mainIntent = new Intent(DSGVOInfo.this, Splash.class);

                DSGVOInfo.this.startActivity(mainIntent);
                DSGVOInfo.this.finish();
            }
        });
    }
}
