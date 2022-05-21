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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Locale;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.MyWebChromeClient;

public class DailytextFragment extends Fragment implements MyWebChromeClient.ProgressListener {

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

    FragmentCommunicationPass fragmentCommunicationPass;
    Toolbar toolbar;
    WebView webView;
    ProgressBar progressBar;
    RelativeLayout errorLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCommunicationPass = (FragmentCommunicationPass) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dailytext, null);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.toolbar);
        webView = view.findViewById(R.id.tt_webview);
        progressBar = view.findViewById(R.id.tt_progress);
        errorLayout = view.findViewById(R.id.tt_error);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        toolbar.setTitle(R.string.title_tt);

        sp = getContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
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
            getView().findViewById(R.id.tt_webview).setVisibility(View.GONE);
            getView().findViewById(R.id.tt_error).setVisibility(View.VISIBLE);
            getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(
                    "android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    @Override
    public void onResume() {
        init();
        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }
    }

    public boolean isConnectedtoNet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }

        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.setVisibility(View.VISIBLE);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                // add progress bar
                webView.setWebChromeClient(new MyWebChromeClient(DailytextFragment.this));
                webView.setWebViewClient(new WebViewClient() {

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
                        progressBar.setIndeterminate(false);
                        progressBar.setVisibility(View.VISIBLE);

                        view.loadUrl("javascript:var header = document.getElementById(\"regionHeader\"); header.parentNode.removeChild(header);");
                        view.loadUrl("javascript:var footer = document.getElementById(\"regionFooter\"); footer.parentNode.removeChild(footer);");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            view.evaluateJavascript("document.getElementById(\"regionMain\").style.marginTop=\"0px\";", null);
                        } else {
                            view.loadUrl("javascript:(function()%7Bdocument.getElementById(\"regionMain\").style.marginTop %3D \"0px\"%7D)();");
                        }

                        errorLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        progressBar.setVisibility(View.GONE);

                        view.loadUrl("javascript:var header = document.getElementById(\"regionHeader\"); header.parentNode.removeChild(header);");
                        view.loadUrl("javascript:var footer = document.getElementById(\"regionFooter\"); footer.parentNode.removeChild(footer);");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            view.evaluateJavascript("document.getElementById(\"regionMain\").style.marginTop=\"0px\";", null);
                        } else {
                            view.loadUrl("javascript:(function()%7Bdocument.getElementById(\"regionMain\").style.marginTop %3D \"0px\"%7D)();");
                        }
                        loaded = true;
                        errorLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);

                        webView.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        loaded = false;
                    }
                });
                webView.loadUrl(url);
            }
        });

    }

    @Override
    public void onUpdateProgress(int progressValue) {
        progressBar.setProgress(progressValue);
        if (progressValue == 100) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


}
