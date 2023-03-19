package tk.phili.dienst.dienst.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.Splash;

public class GDPRInfo extends AppCompatActivity {

    private static final String GDPR_URL = "https://ministryapp.de/privacy";

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
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                if(!url.equals(GDPR_URL)) {
                    web.loadUrl(GDPR_URL);
                }
                super.doUpdateVisitedHistory(view, url, isReload);
            }
        });

        web.loadUrl(GDPR_URL);

        findViewById(R.id.accept_dsgvo).setOnClickListener(view -> {
            editor.putBoolean("dsgvo_accept", true);
            editor.commit();

            Intent mainIntent = new Intent(GDPRInfo.this, Splash.class);

            GDPRInfo.this.startActivity(mainIntent);
            GDPRInfo.this.finish();
        });
    }
}
