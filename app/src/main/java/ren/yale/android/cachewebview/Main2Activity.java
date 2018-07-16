package ren.yale.android.cachewebview;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;

import java.io.File;

import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.utils.NetUtils;

public class Main2Activity extends Activity {

    public static final String KEY_URL = "";
    private CacheWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mWebView = findViewById(R.id.webview);
        String url = getIntent().getStringExtra(KEY_URL);

        initSettings();

        String html = "<html><div class=\"line\"><img src=\"/bbs/upload/1000/2018/03/21/32873_1025080screenshot_2018-03-21-10-22-54-916_com.unicom.woshipin.png\" width=\"400px\" alt=\"screenshot_2018-03-21-10-22-54-916_com.unicom.woshipin\"></div></html>";
        mWebView.loadDataWithBaseURL("http://yaohw.com", html, "text/html", "utf-8", "");
    }


    private void initSettings() {
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);

        webSettings.setLoadWithOverviewMode(true);

        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        webSettings.setDefaultTextEncodingName("UTF-8");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }
        if (NetUtils.isConnected(mWebView.getContext())) {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        } else {
            webSettings.setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(
                    WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        setCachePath();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mWebView.clearCache();

    }

    private void setCachePath() {

        File cacheFile = new File(mWebView.getContext().getCacheDir(), "appcache_name");
        String path = cacheFile.getAbsolutePath();

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(path);
    }
}
