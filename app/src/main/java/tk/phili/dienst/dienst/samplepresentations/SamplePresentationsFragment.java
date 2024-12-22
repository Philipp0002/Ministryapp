package tk.phili.dienst.dienst.samplepresentations;

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

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.MyWebChromeClient;
import tk.phili.dienst.dienst.utils.Utils;


public class SamplePresentationsFragment extends Fragment implements MyWebChromeClient.ProgressListener {

    private Toolbar toolbar;
    private SharedPreferences sp;

    boolean pageSuccess = true;
    public String lastUrl = "";

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utils.isConnectedtoNet(getContext())) {
                tryLoadPage();
            }
        }
    };

    FragmentCommunicationPass fragmentCommunicationPass;
    WebView webView;
    ProgressBar progressBar;

    JSONObject serverResponse = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCommunicationPass = (FragmentCommunicationPass) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sample_presentations, null);
        return root;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.toolbar);
        webView = view.findViewById(R.id.samplePresentationsWebView);
        progressBar = view.findViewById(R.id.samplePresentationsProgressBar);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        sp = getContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // add progress bar
        webView.setWebChromeClient(new MyWebChromeClient(SamplePresentationsFragment.this));
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals(lastUrl) || url.contains("/d/")) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!pageSuccess) return;
                pageSuccess = true;
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!pageSuccess) return;
                pageSuccess = true;
                progressBar.setVisibility(View.GONE);
                view.loadUrl("javascript:var header = document.getElementById(\"regionHeader\"); header.parentNode.removeChild(header);");
                view.loadUrl("javascript:var footer = document.getElementById(\"regionFooter\"); footer.parentNode.removeChild(footer);");
                view.loadUrl("javascript:var main = document.getElementById(\"regionMain\"); main.classList.remove(\"showSecondaryNav\"); main.classList.remove(\"hasSecondaryNav\");");
                view.loadUrl("javascript:var style = document.createElement('style'); style.innerHTML = \".lnc-firstRunPopup {display: none !important;} .articlePositioner {float: none !important; margin-top: 16px;}\"; document.head.appendChild(style);");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript("document.getElementById(\"regionMain\").style.marginTop=\"0px\";", null);
                } else {
                    view.loadUrl("javascript:(function()%7Bdocument.getElementById(\"regionMain\").style.marginTop %3D \"0px\"%7D)();");
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                pageSuccess = false;
                if (Utils.isConnectedtoNet(getContext())) {
                    tryLoadPage();
                } else {
                    showErrorState();
                }
            }
        });

        if (Utils.isConnectedtoNet(getContext())) {
            tryLoadPage();
        } else {
            showErrorState();
        }
    }

    public void tryLoadPage() {
        View nonet = getView().findViewById(R.id.samplePresentationsErrorContainer);
        nonet.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.VISIBLE);
        try {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
        }

        String lang = sp.getString("sample_presentations_locale", Locale.getDefault().getLanguage());

        switch (lang) {
            case "de":
                lastUrl = "https://wol.jw.org/de/wol/d/r10/lp-x/1102023316";
                break;
            case "tr":
                lastUrl = "https://wol.jw.org/tr/wol/d/r22/lp-tk/1102023316";
                break;
            case "fr":
                lastUrl = "https://wol.jw.org/fr/wol/d/r30/lp-f/1102023316";
                break;
            case "it":
                lastUrl = "https://wol.jw.org/it/wol/d/r6/lp-i/1102023316";
                break;
            case "th":
                lastUrl = "https://wol.jw.org/th/wol/d/r113/lp-si/1102023316";
                break;
            case "pl":
                lastUrl = "https://wol.jw.org/pl/wol/d/r12/lp-p/1102023316";
                break;
            case "es":
                lastUrl = "https://wol.jw.org/es/wol/d/r4/lp-s/1102023316";
                break;
            case "el":
                lastUrl = "https://wol.jw.org/el/wol/d/r11/lp-g/1102023316";
                break;
            default:
                lastUrl = "https://wol.jw.org/en/wol/d/r1/lp-e/1102023316";
                break;
        }

        webView.loadUrl(lastUrl);
    }

    @Override
    public void onUpdateProgress(int progressValue) {
        progressBar.setProgress(progressValue);
        if (progressValue == 100) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    public void showErrorState() {
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(
                "android.net.conn.CONNECTIVITY_CHANGE"));

        getView().findViewById(R.id.samplePresentationsErrorContainer).setVisibility(View.VISIBLE);
        webView.setVisibility(View.INVISIBLE);
    }


    public int getYear() {
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        return thisYear;
    }


    public int getMonth() {
        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(Calendar.MONTH);
        return thisMonth;
    }


    @Override
    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}
