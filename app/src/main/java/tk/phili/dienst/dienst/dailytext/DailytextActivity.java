package tk.phili.dienst.dienst.dailytext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Locale;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.drawer.Drawer;
import tk.phili.dienst.dienst.utils.MyWebChromeClient;

public class DailytextActivity extends AppCompatActivity implements MyWebChromeClient.ProgressListener {

    public SharedPreferences sp;
    private SharedPreferences.Editor editor;
    boolean loaded = false;

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isConnectedtoNet()) {
                int d = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int m = Calendar.getInstance().get(Calendar.MONTH) + 1;
                int y = Calendar.getInstance().get(Calendar.YEAR);

                String locale = sp.getString("tt_locale", Locale.getDefault().getLanguage());

                showLink("https://ministryapp.de/dailytext_fwd.php?d=" + d + "&m=" + m + "&y=" + y + "&l=" + locale);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagestext);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();

        Drawer.addDrawer(this, toolbar, 5);

        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        init();

    }

    public void init(){
        if(loaded)return;
        if(isConnectedtoNet()) {
            int d = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int m = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int y = Calendar.getInstance().get(Calendar.YEAR);

            String locale = sp.getString("tt_locale", Locale.getDefault().getLanguage());
            showLink("https://ministryapp.de/dailytext_fwd.php?d=" + d + "&m=" + m + "&y=" + y + "&l=" + locale);
        }else{
            findViewById(R.id.tt_webview).setVisibility(View.GONE);
            findViewById(R.id.tt_error).setVisibility(View.VISIBLE);
            registerReceiver(mBroadcastReceiver, new IntentFilter(
                    "android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    @Override
    protected void onResume() {
        init();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }
    }

    public boolean isConnectedtoNet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }else {
            return false;
        }

    }


    public void showLink(final String url){
        try {
            unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }

        final WebView mWebView = (WebView) findViewById(R.id.tt_webview);
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.tt_webview).setVisibility(View.VISIBLE);
                WebSettings webSettings = mWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                // add progress bar
                mWebView.setWebChromeClient(new MyWebChromeClient(DailytextActivity.this));
                mWebView.setWebViewClient(new WebViewClient() {

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (url.contains("dt") || url.contains("dienstapp")) {
                            return false;
                        }else{
                            return true;
                        }
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        ((ProgressBar) findViewById(R.id.tt_progress)).setIndeterminate(false);
                        ((ProgressBar) findViewById(R.id.tt_progress)).setVisibility(View.VISIBLE);

                        view.loadUrl("javascript:var header = document.getElementById(\"regionHeader\"); header.parentNode.removeChild(header);");
                        view.loadUrl("javascript:var footer = document.getElementById(\"regionFooter\"); footer.parentNode.removeChild(footer);");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            view.evaluateJavascript("document.getElementById(\"regionMain\").style.marginTop=\"0px\";", null);
                        } else {
                            view.loadUrl("javascript:(function()%7Bdocument.getElementById(\"regionMain\").style.marginTop %3D \"0px\"%7D)();");
                        }

                        findViewById(R.id.tt_error).setVisibility(View.GONE);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        ((ProgressBar) findViewById(R.id.tt_progress)).setVisibility(View.GONE);

                        view.loadUrl("javascript:var header = document.getElementById(\"regionHeader\"); header.parentNode.removeChild(header);");
                        view.loadUrl("javascript:var footer = document.getElementById(\"regionFooter\"); footer.parentNode.removeChild(footer);");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            view.evaluateJavascript("document.getElementById(\"regionMain\").style.marginTop=\"0px\";", null);
                        } else {
                            view.loadUrl("javascript:(function()%7Bdocument.getElementById(\"regionMain\").style.marginTop %3D \"0px\"%7D)();");
                        }
                        loaded = true;
                        findViewById(R.id.tt_error).setVisibility(View.GONE);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);

                        findViewById(R.id.tt_webview).setVisibility(View.GONE);
                        findViewById(R.id.tt_error).setVisibility(View.VISIBLE);
                        loaded = false;
                    }
                });
                mWebView.loadUrl(url);
            }
        });

    }

    @Override
    public void onUpdateProgress(int progressValue) {
        ((ProgressBar)findViewById(R.id.tt_progress)).setProgress(progressValue);
        if (progressValue == 100) {
            ((ProgressBar)findViewById(R.id.tt_progress)).setVisibility(View.INVISIBLE);
        }
    }


}
