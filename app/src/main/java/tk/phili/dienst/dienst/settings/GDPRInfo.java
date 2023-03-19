package tk.phili.dienst.dienst.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.Splash;

public class GDPRInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gdpr_info);

        SharedPreferences sp = getSharedPreferences("Splash", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        boolean b = getIntent().getBooleanExtra("hastoaccept", true);

        if (!b) {
            findViewById(R.id.bottombar).setVisibility(View.GONE);
        }

        WebView web = findViewById(R.id.webView);
        web.loadUrl("https://ministryapp.de/privacy");

        findViewById(R.id.accept_dsgvo).setOnClickListener(view -> {
            editor.putBoolean("dsgvo_accept", true);
            editor.commit();

            Intent mainIntent = new Intent(GDPRInfo.this, Splash.class);

            GDPRInfo.this.startActivity(mainIntent);
            GDPRInfo.this.finish();
        });
    }
}
