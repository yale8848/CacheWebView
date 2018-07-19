package ren.yale.android.cachewebviewlib;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by yale on 2018/7/13.
 */
public interface WebViewRequestInterceptor {
    WebResourceResponse interceptRequest(WebView view, WebResourceRequest request);
    WebResourceResponse interceptRequest(WebView view, String url);
    File getCachePath();

    void clearCache();
    void enableForce(boolean force);
    InputStream getCacheFile(String url);
    void loadUrl(WebView webView ,String url);
    void loadUrl(WebView webView ,String url, Map<String, String> additionalHttpHeaders);

}
