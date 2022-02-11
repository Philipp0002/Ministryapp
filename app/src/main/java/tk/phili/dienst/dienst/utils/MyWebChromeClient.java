package tk.phili.dienst.dienst.utils;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by fipsi on 02.08.2017.
 */

public class MyWebChromeClient extends WebChromeClient {
    private ProgressListener mListener;

    public MyWebChromeClient(ProgressListener listener) {
        mListener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mListener.onUpdateProgress(newProgress);
        super.onProgressChanged(view, newProgress);
    }


    public interface ProgressListener {
        public void onUpdateProgress(int progressValue);
    }
}
