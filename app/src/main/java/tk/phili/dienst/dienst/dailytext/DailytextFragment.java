package tk.phili.dienst.dienst.dailytext;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.JWLanguageService;
import tk.phili.dienst.dienst.utils.MyWebChromeClient;
import tk.phili.dienst.dienst.utils.Utils;

public class DailytextFragment extends Fragment implements MyWebChromeClient.ProgressListener {

    public SharedPreferences sp;
    boolean loaded = false;

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Utils.isConnectedtoNet(getContext())) {
                showLink();
            }
        }
    };

    private FragmentCommunicationPass fragmentCommunicationPass;
    private Toolbar toolbar;
    private WebView webView;
    private ProgressBar progressBar;
    private View errorLayout;
    private JWLanguageService languageService;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCommunicationPass = (FragmentCommunicationPass) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dailytext, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.toolbar);
        webView = view.findViewById(R.id.dailytextWebView);
        progressBar = view.findViewById(R.id.dailytextProgressBar);
        errorLayout = view.findViewById(R.id.dailytextErrorContainer);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        sp = requireContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        languageService = new JWLanguageService(requireContext());

        init();
    }

    public void init(){
        if(loaded) return;
        if(Utils.isConnectedtoNet(getContext())) {
            showLink();
        }else{
            requireView().findViewById(R.id.dailytextWebView).setVisibility(View.GONE);
            requireView().findViewById(R.id.dailytextErrorContainer).setVisibility(View.VISIBLE);
            requireActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(
                    "android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    @Override
    public void onResume() {
        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            requireActivity().unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            requireActivity().unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }
    }

    public void showLink(){
        String jwLang = sp.getString("tt_locale", languageService.getCurrentLanguage("E").getLangcode());
        String url = "https://www.jw.org/finder?srcid=jwlshare&wtlocale="+ jwLang +"&alias=daily-text&date="+LocalDate.now().toString();
        try {
            requireActivity().unregisterReceiver(mBroadcastReceiver);
        }catch(Exception e){ }

        webView.post(new Runnable() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void run() {
                webView.setVisibility(View.VISIBLE);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                // add progress bar
                webView.setWebChromeClient(new MyWebChromeClient(DailytextFragment.this));
                webView.setWebViewClient(new WebViewClient() {

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return url.contains("https://wol.jw.org") && webView.getUrl().contains("https://wol.jw.org");
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        progressBar.setIndeterminate(false);
                        progressBar.setVisibility(View.VISIBLE);

                        errorLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        progressBar.setVisibility(View.GONE);

                        view.loadUrl("javascript:var header = document.getElementById(\"regionHeader\"); header.parentNode.removeChild(header);");
                        view.loadUrl("javascript:var welcome = document.getElementById(\"welcome\"); welcome.parentNode.removeChild(welcome);");
                        view.loadUrl("javascript:var todayNav = document.getElementById(\"todayNav\"); todayNav.parentNode.removeChild(todayNav);");
                        view.loadUrl("javascript:var footer = document.getElementById(\"regionFooter\"); footer.parentNode.removeChild(footer);");
                        view.loadUrl("javascript:var style = document.createElement('style'); style.innerHTML = \".lnc-firstRunPopup {display: none !important;}\"; document.head.appendChild(style);");
                        view.loadUrl("javascript:document.getElementsByClassName(\"todayItem\")[1].remove();");
                        view.loadUrl("javascript:document.getElementsByClassName(\"todayItem\")[1].remove();");
                        view.loadUrl("javascript:document.getElementsByClassName(\"todayItem\")[1].remove();");
                        view.evaluateJavascript("document.getElementById(\"regionMain\").style.marginTop=\"0px\";", null);
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
