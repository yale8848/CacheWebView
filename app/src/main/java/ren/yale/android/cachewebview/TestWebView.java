package ren.yale.android.cachewebview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import java.io.File;

import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.utils.NetworkUtils;


/**
 * Created by yale on 2017/9/30.
 */

public class TestWebView extends RelativeLayout {
    private CacheWebView mDXHWebView = null;
    private Context mContext;

    public TestWebView(Context context) {
        super(context);
        init(context);
    }

    public TestWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TestWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public CacheWebView getDXHWebView() {
        return mDXHWebView;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private void addWebView(Context context) {
        mDXHWebView = new CacheWebView(context);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(mDXHWebView, rl);
    }

    private void initSettings() {
        WebSettings webSettings = mDXHWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 16) {
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        if (NetworkUtils.isConnected(mContext)) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webSettings.setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);


        }
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        webSettings.setAppCacheEnabled(true);


        //File fDatabase = new File(mContext.getCacheDir().getAbsolutePath(),"webview_db");
        //webSettings.setDatabasePath(fDatabase.getAbsolutePath());
        File fAppCache = new File(mContext.getCacheDir().getAbsolutePath(), "webview_cache");
        webSettings.setAppCachePath(fAppCache.getAbsolutePath());

        webSettings.setBuiltInZoomControls(true);// api-3
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= 11) {
            webSettings.setDisplayZoomControls(false);// api-11
        }

        //http://wiki.jikexueyuan.com/project/chrome-devtools/remote-debugging-on-android.html
        if (Build.VERSION.SDK_INT >= 19) {//for chrome debug
            WebView.setWebContentsDebuggingEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(
                    WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
    }

    private void init(final Context context) {

        mContext = context;
        addWebView(context);

        initSettings();

    }
}
