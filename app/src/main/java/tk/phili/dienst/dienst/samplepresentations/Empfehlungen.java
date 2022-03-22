package tk.phili.dienst.dienst.samplepresentations;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.drawer.Drawer;
import tk.phili.dienst.dienst.utils.MyWebChromeClient;


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
        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.setVisibility(View.VISIBLE);
        try {
            unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }

        String urlend = sp.getString("sample_presentations_locale", Locale.getDefault().getLanguage());

        final Empfehlungen em = this;


        final EmpfehlungenAsyncFetcher asyncFetcher = new EmpfehlungenAsyncFetcher();
        asyncFetcher.language = urlend;
        asyncFetcher.futurerun = new Runnable() {
            @Override
            public void run() {
                JSONObject obj = asyncFetcher.response;

                if(obj != null) {
                    final Spinner spinner = (Spinner) findViewById(R.id.spinner_nav_empf);
                    Empfehlungen.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner.setVisibility(View.VISIBLE);
                        }
                    });

                    ArrayList<String> listItems = new ArrayList<String>();
                    final ArrayList<String> listUrls = new ArrayList<String>();
                    JSONArray monthsArray = null;
                    String baseURL = "";
                    try {
                        monthsArray = obj.getJSONArray("months");
                        baseURL = obj.getString("baseURL");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    for (int i = 0; i < monthsArray.length(); i++) {
                        listItems.add(new DateFormatSymbols().getMonths()[i]);
                        try {
                            listUrls.add(baseURL + monthsArray.getJSONObject(i).getString("url"));
                        } catch (JSONException e) { e.printStackTrace(); }
                    }

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(em,
                            R.layout.spinner_main, listItems);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);




                    Empfehlungen.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner.setAdapter(adapter);
                            if(listItems.size() > getMonth()){
                                spinner.setSelection(getMonth());
                            }else{
                                Toast.makeText(Empfehlungen.this, getString(R.string.empf_not_available), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

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

                            lasturl = listUrls.get(position);
                            mWebView.loadUrl(listUrls.get(position));

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });



                }
            }
        };
        asyncFetcher.execute();

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


    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }
        super.onDestroy();
    }
}
