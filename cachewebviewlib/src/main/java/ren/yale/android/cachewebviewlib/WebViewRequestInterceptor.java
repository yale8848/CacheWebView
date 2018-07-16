package ren.yale.android.cachewebviewlib;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.File;

/**
 * Created by yale on 2018/7/13.
 */
public interface WebViewRequestInterceptor {
    WebResourceResponse interceptRequest(WebView view, WebResourceRequest request);
    WebResourceResponse interceptRequest(WebView view, String url);
    File getCachePath();
    void loadUrl(WebView webView ,String url);
    void clearCache();
    void enableForce(boolean force);
    boolean getCacheFile(String url, File desPath);
}
