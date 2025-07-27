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

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.JWLanguageService;
import tk.phili.dienst.dienst.utils.MyWebChromeClient;
import tk.phili.dienst.dienst.utils.Utils;


public class SamplePresentationsFragment extends Fragment implements MyWebChromeClient.ProgressListener {

    private Toolbar toolbar;
    private SharedPreferences sp;

    public String lastUrl = "";


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utils.isConnectedtoNet(getContext())) {
                tryLoadPage();
            }
        }
    };

    private FragmentCommunicationPass fragmentCommunicationPass;
    private WebView webView;
    private ProgressBar progressBar;

    private JWLanguageService languageService;

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

        languageService = new JWLanguageService(requireContext());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // add progress bar
        webView.setWebChromeClient(new MyWebChromeClient(SamplePresentationsFragment.this));
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return webView.getOriginalUrl() != null;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                view.loadUrl("javascript:var header = document.getElementById(\"mobileNavTopBar\"); header.parentNode.removeChild(header);");
                view.loadUrl("javascript:var header2 = document.getElementById(\"regionHeader\"); header2.parentNode.removeChild(header2);");
                view.loadUrl("javascript:var header3 = document.getElementById(\"regionPrimaryNav\"); header3.parentNode.removeChild(header3);");
                view.loadUrl("javascript:var sidebar = document.getElementById(\"sidebar\"); sidebar.parentNode.removeChild(sidebar);");
                view.loadUrl("javascript:var breadcrumbs = document.getElementsByClassName(\"breadcrumbs\")[0]; breadcrumbs.parentNode.removeChild(breadcrumbs);");
                view.loadUrl("javascript:var articleFooterLinks = document.getElementsByClassName(\"articleFooterLinks\")[0]; articleFooterLinks.parentNode.removeChild(articleFooterLinks);");
                view.loadUrl("javascript:var footer = document.getElementsByTagName(\"footer\")[0]; footer.parentNode.removeChild(footer);");
                view.loadUrl("javascript:var style = document.createElement('style'); style.innerHTML = \".lnc-firstRunPopup {display: none !important;} .articlePositioner {float: none !important; margin-top: 16px;}\"; document.head.appendChild(style);");
                view.evaluateJavascript("document.getElementById(\"regionMain\").style.marginTop=\"0px\";", null);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
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
        View nonet = requireView().findViewById(R.id.samplePresentationsErrorContainer);
        nonet.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.VISIBLE);
        try {
            requireActivity().unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
        }

        String jwLang = sp.getString("sample_presentations_locale", languageService.getCurrentLanguage("E").getLangcode());
        lastUrl = "https://www.jw.org/finder?wtlocale="+ jwLang +"&docid=1102023316&srctype=wol";

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
