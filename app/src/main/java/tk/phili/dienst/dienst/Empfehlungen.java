package tk.phili.dienst.dienst;

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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class Empfehlungen extends AppCompatActivity implements MyWebChromeClient.ProgressListener {


    private Toolbar toolbar;
    MenuItem bericht;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    boolean pageSuccess = true;

    private ActionBarDrawerToggle actionbartoggle;

    public ArrayList<String> dates = new ArrayList<String>();

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isConnectedtoNet()) {
                setSpinnerText();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empfehlungen);
        setTitle(getResources().getString(R.string.title_section4));
        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        if(isConnectedtoNet()) {
            setSpinnerText();
        }else{
            setErrorSpinner();
        }

        /////////////////DRAWER/////////////////////////////////////////
        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_3);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        Drawer.addDrawer(this, toolbar, 4);
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

    public String lasturl = "";


    public void setSpinnerText(){
        TextView nonet = (TextView) findViewById(R.id.no_net);
        nonet.setVisibility(View.INVISIBLE);
        ImageView pic = (ImageView) findViewById(R.id.imageView3);
        pic.setVisibility(View.INVISIBLE);
        /*Button retry = (Button) findViewById(R.id.retry_empf);
        retry.setVisibility(View.INVISIBLE);*/
        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.setVisibility(View.VISIBLE);
        try {
            unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }

        String urlend = Locale.getDefault().getLanguage();
        final Empfehlungen em = this;
        //GET DATESarray
        Ion.with(getApplicationContext())
                .load("https://dienstapp.raffaelhahn.de/whatempfehlungen.php?lang=" + urlend)
                .noCache()
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            String res = result;
                            String[] tags = res.split("_");

                            final Spinner spinner = (Spinner) findViewById(R.id.spinner_nav_empf);
                            spinner.setVisibility(View.VISIBLE);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(em,
                                    R.layout.spinner_main, tags);

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinner.setAdapter(adapter);

                            String thismonthname = monthnameconverter(getMonth() + "", false);

                            try {
                                spinner.setSelection(getMonth());//Locale.getDefault().getLanguage();
                            }catch(Exception ex){
                                Toast.makeText(Empfehlungen.this, getString(R.string.empf_not_available), Toast.LENGTH_SHORT);
                            }

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    String monthname = spinner.getItemAtPosition(position).toString();
                                    String number = (position+1)+"";//monthnameconverter(monthname, true);

                                    WebView mWebView = (WebView) findViewById(R.id.webView);
                                    WebSettings webSettings = mWebView.getSettings();
                                    webSettings.setJavaScriptEnabled(true);

                                    // add progress bar
                                    mWebView.setWebChromeClient(new MyWebChromeClient(Empfehlungen.this));
                                    mWebView.setWebViewClient(new WebViewClient() {

                                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                            if (url.equals(lasturl) || url.contains("/d/")) {
                                                return false;
                                            } else {
                                                return true;
                                            }
                                        }

                                        @Override
                                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                            super.onPageStarted(view, url, favicon);
                                            if(!pageSuccess)return;
                                            pageSuccess = true;
                                            ((ProgressBar) findViewById(R.id.progressBar_empf)).setVisibility(View.VISIBLE);
                                            view.loadUrl("javascript:var header = document.getElementById(\"regionHeader\"); header.parentNode.removeChild(header);");
                                            view.loadUrl("javascript:var footer = document.getElementById(\"regionFooter\"); footer.parentNode.removeChild(footer);");
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                view.evaluateJavascript("document.getElementById(\"regionMain\").style.marginTop=\"0px\";", null);
                                            } else {
                                                view.loadUrl("javascript:(function()%7Bdocument.getElementById(\"regionMain\").style.marginTop %3D \"0px\"%7D)();");
                                            }
                                        }

                                        @Override
                                        public void onPageFinished(WebView view, String url) {
                                            super.onPageFinished(view, url);
                                            if(!pageSuccess)return;
                                            pageSuccess = true;
                                            ((ProgressBar) findViewById(R.id.progressBar_empf)).setVisibility(View.GONE);
                                            view.loadUrl("javascript:var header = document.getElementById(\"regionHeader\"); header.parentNode.removeChild(header);");
                                            view.loadUrl("javascript:var footer = document.getElementById(\"regionFooter\"); footer.parentNode.removeChild(footer);");
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                view.evaluateJavascript("document.getElementById(\"regionMain\").style.marginTop=\"0px\";", null);
                                            } else {
                                                view.loadUrl("javascript:(function()%7Bdocument.getElementById(\"regionMain\").style.marginTop %3D \"0px\"%7D)();");
                                            }

                                        }


                                        @Override
                                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                                            pageSuccess = false;
                                            if(isConnectedtoNet()) {
                                                setSpinnerText();
                                            }else{
                                                setErrorSpinner();
                                            }

                                        }
                                    });
                                    lasturl = "https://dienstapp.raffaelhahn.de/empfehlungen.php?lang=" + Locale.getDefault().getLanguage() + "&month=" + number + "&year=" + getYear();
                                    mWebView.loadUrl("https://dienstapp.raffaelhahn.de/empfehlungen.php?lang=" + Locale.getDefault().getLanguage() + "&month=" + number + "&year=" + getYear());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                    // your code here
                                }

                            });
                        }catch(Exception exce){ }
                    }
                });








    }

    @Override
    public void onUpdateProgress(int progressValue) {
        ((ProgressBar)findViewById(R.id.progressBar_empf)).setProgress(progressValue);
        if (progressValue == 100) {
            ((ProgressBar)findViewById(R.id.progressBar_empf)).setVisibility(View.INVISIBLE);
        }
    }




    public void setErrorSpinner(){
        registerReceiver(mBroadcastReceiver, new IntentFilter(
                "android.net.conn.CONNECTIVITY_CHANGE"));

        String[] tags = new String[]{getString(R.string.error)};

        final Spinner spinner = (Spinner) findViewById(R.id.spinner_nav_empf);
        spinner.setVisibility(View.INVISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_main, tags);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        TextView nonet = (TextView) findViewById(R.id.no_net);
        nonet.setVisibility(View.VISIBLE);
        ImageView pic = (ImageView) findViewById(R.id.imageView3);
        pic.setVisibility(View.VISIBLE);
        //Button retry = (Button) findViewById(R.id.retry_empf);
        //retry.setVisibility(View.VISIBLE);
        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.setVisibility(View.INVISIBLE);



        /*retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectedtoNet()) {
                    setSpinnerText();
                }else{
                    setErrorSpinner();
                }
            }
        });*/
    }


    public int getYear(){
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        return thisYear;
    }


    public int getMonth(){
        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(Calendar.MONTH);
        return thisMonth;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empfehlungen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/

        if (actionbartoggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public String monthnameconverter(String month, boolean tonumber){
        if(tonumber){
            if(month.equalsIgnoreCase(getString(R.string.monat1))){
                return "1";
            }
            if(month.equalsIgnoreCase(getString(R.string.monat2))){
                return "2";
            }
            if(month.equalsIgnoreCase(getString(R.string.monat3))){
                return "3";
            }
            if(month.equalsIgnoreCase(getString(R.string.monat4))){
                return "4";
            }
            if(month.equalsIgnoreCase(getString(R.string.monat5))){
                return "5";
            }
            if(month.equalsIgnoreCase(getString(R.string.monat6))){
                return "6";
            }
            if(month.equalsIgnoreCase(getString(R.string.monat7))){
                return "7";
            }
            if(month.equalsIgnoreCase(getString(R.string.monat8))){
                return "8";
            }
            if(month.equalsIgnoreCase(getString(R.string.monat9))){
                return "9";
            }
            if(month.equalsIgnoreCase(getString(R.string.monat10))){
                return "10";
            }
            if(month.equalsIgnoreCase(getString(R.string.monat11))){
                return "11";
            }
            if(month.equalsIgnoreCase(getString(R.string.monat12))){
                return "12";
            }
        }
        if(!tonumber){
            if(month.equalsIgnoreCase("1")){
                return getString(R.string.monat1);
            }
            if(month.equalsIgnoreCase("2")){
                return getString(R.string.monat2);
            }
            if(month.equalsIgnoreCase("3")){
                return getString(R.string.monat3);
            }
            if(month.equalsIgnoreCase("4")){
                return getString(R.string.monat4);
            }
            if(month.equalsIgnoreCase("5")){
                return getString(R.string.monat5);
            }
            if(month.equalsIgnoreCase("6")){
                return getString(R.string.monat6);
            }
            if(month.equalsIgnoreCase("7")){
                return getString(R.string.monat7);
            }
            if(month.equalsIgnoreCase("8")){
                return getString(R.string.monat8);
            }
            if(month.equalsIgnoreCase("9")){
                return getString(R.string.monat9);
            }
            if(month.equalsIgnoreCase("10")){
                return getString(R.string.monat10);
            }
            if(month.equalsIgnoreCase("11")){
                return getString(R.string.monat11);
            }
            if(month.equalsIgnoreCase("12")){
                return getString(R.string.monat12);
            }
        }
        return "FEHLER";
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }
        super.onDestroy();
    }
}
