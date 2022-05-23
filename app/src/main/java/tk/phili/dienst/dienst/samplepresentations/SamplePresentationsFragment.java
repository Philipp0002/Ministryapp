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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.MyWebChromeClient;


public class SamplePresentationsFragment extends Fragment implements MyWebChromeClient.ProgressListener {


    private Toolbar toolbar;
    public SharedPreferences sp;

    boolean pageSuccess = true;
    public String lasturl = "";


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isConnectedtoNet()) {
                setSpinnerText();
            }
        }
    };

    FragmentCommunicationPass fragmentCommunicationPass;
    Spinner spinner;
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.toolbar);
        spinner = view.findViewById(R.id.spinner_nav_empf);
        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressBar_empf);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        toolbar.setTitle(getResources().getString(R.string.title_section4));
        sp = getContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // add progress bar
        webView.setWebChromeClient(new MyWebChromeClient(SamplePresentationsFragment.this));
        webView.setWebViewClient(new WebViewClient() {

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
                if (!pageSuccess) return;
                pageSuccess = true;
                progressBar.setVisibility(View.VISIBLE);
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
                if (!pageSuccess) return;
                pageSuccess = true;
                progressBar.setVisibility(View.GONE);
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
                if (isConnectedtoNet()) {
                    setSpinnerText();
                } else {
                    setErrorSpinner();
                }

            }
        });

        if (isConnectedtoNet()) {
            setSpinnerText();
        } else {
            setErrorSpinner();
        }
    }

    public boolean isConnectedtoNet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else {
            return false;
        }

    }


    public void setSpinnerText() {
        TextView nonet = getView().findViewById(R.id.no_net);
        ImageView pic = getView().findViewById(R.id.imageView3);
        nonet.setVisibility(View.INVISIBLE);
        pic.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.VISIBLE);
        try {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
        }

        String urlend = sp.getString("sample_presentations_locale", Locale.getDefault().getLanguage());

        final SamplePresentationsAsyncFetcher asyncFetcher = new SamplePresentationsAsyncFetcher();
        asyncFetcher.language = urlend;
        asyncFetcher.futurerun = () -> {
            resolveResponse(asyncFetcher.response);
        };
        asyncFetcher.execute();
    }

    public void resolveResponse(JSONObject serverResponse) {
        this.serverResponse = serverResponse;
        if (serverResponse != null) {
            if(getActivity() == null)
                return;

            getActivity().runOnUiThread(() -> spinner.setVisibility(View.VISIBLE));

            ArrayList<String> listItems = new ArrayList<String>();
            final ArrayList<String> listUrls = new ArrayList<String>();
            JSONArray monthsArray = null;
            String baseURL = "";
            try {
                monthsArray = serverResponse.getJSONArray("months");
                baseURL = serverResponse.getString("baseURL");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            for (int i = 0; i < monthsArray.length(); i++) {
                listItems.add(new DateFormatSymbols().getMonths()[i]);
                try {
                    listUrls.add(baseURL + monthsArray.getJSONObject(i).getString("url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    R.layout.spinner_main, listItems);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            getActivity().runOnUiThread(() -> {
                spinner.setAdapter(adapter);
                if (listItems.size() > getMonth()) {
                    spinner.setSelection(getMonth());
                } else {
                    Toast.makeText(getContext(), getString(R.string.empf_not_available), Toast.LENGTH_SHORT).show();
                }
            });


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean initial = true;

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (initial) {
                        initial = false;
                        return;
                    }
                    lasturl = listUrls.get(position);
                    webView.loadUrl(lasturl);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }

            });


            lasturl = listUrls.get(getMonth());
            getActivity().runOnUiThread(() -> webView.loadUrl(lasturl));
        }
    }

    @Override
    public void onUpdateProgress(int progressValue) {
        progressBar.setProgress(progressValue);
        if (progressValue == 100) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    public void setErrorSpinner() {
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(
                "android.net.conn.CONNECTIVITY_CHANGE"));

        String[] tags = new String[]{getString(R.string.error)};

        spinner.setVisibility(View.INVISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_main, tags);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        TextView nonet = getView().findViewById(R.id.no_net);
        nonet.setVisibility(View.VISIBLE);
        ImageView pic = getView().findViewById(R.id.imageView3);
        pic.setVisibility(View.VISIBLE);
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
