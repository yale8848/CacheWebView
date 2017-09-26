package ren.yale.android.cachewebviewlib;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ren.yale.android.cachewebviewlib.utils.FileUtil;
import ren.yale.android.cachewebviewlib.utils.NetworkUtils;


/**
 * Created by yale on 2017/9/15.
 */

public class CacheWebView extends WebView {

    private static final String CACHE_NAME = "CacheWebView";
    private static final int CACHE_SIZE = 200*1024*1024;

    private String mAppCachePath = "";

    private CacheWebViewClient mCacheWebViewClient;

    private HashMap<String,Map> mHeaderMaps;

    public CacheWebView(Context context) {
        this(context,null,0);
    }

    public CacheWebView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CacheWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        initData();
        initSettings();
        initWebViewClient();
    }

    private void initData() {
        mHeaderMaps = new HashMap<>();

        File cacheFile = new File(getContext().getCacheDir(),CACHE_NAME);
        try {
            CacheWebView.getWebViewCache().init(getContext(),cacheFile,CACHE_SIZE).
                    setCacheStrategy(WebViewCache.CacheStrategy.FORCE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static WebViewCache getWebViewCache(){
        return WebViewCache.getInstance();
    }

    public void setWebViewClient(WebViewClient client){
        mCacheWebViewClient.setCustomWebViewClient(client);
    }

    private void initWebViewClient() {
        mCacheWebViewClient = new CacheWebViewClient();
       super.setWebViewClient(mCacheWebViewClient);
    }

    public static void preLoad(Context context,String url){
        CacheWebView webView = new CacheWebView(context);
        webView.loadUrl(url);
    }

    public void loadUrl(String url){
        super.loadUrl(url);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        mHeaderMaps.put(url,additionalHttpHeaders);
        getWebViewCache().addHeaderMap(url,additionalHttpHeaders);
        super.loadUrl(url,additionalHttpHeaders);
    }

    private void initSettings(){
        WebSettings webSettings = this.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);

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
        if (NetworkUtils.isConnected(this.getContext()) ){
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
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

    private void setCachePath(){

        File  cacheFile = new File(this.getContext().getCacheDir(),CACHE_NAME);
        String path = cacheFile.getAbsolutePath();
        mAppCachePath = path;

        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }

        WebSettings webSettings = this.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(path);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(path);
    }
    public String getAppCachePath(){
        return mAppCachePath;
    }

    public void clearCache(){

        FileUtil.deleteDirs(mAppCachePath,false);
        getWebViewCache().clean();
    }

    public void destroy(){

        getWebViewCache().clearHeaderMap(mHeaderMaps);
        mHeaderMaps.clear();
        mHeaderMaps = null;

        ViewParent viewParent = this.getParent();
        if (viewParent == null){
            super.destroy();
            return ;
        }
        ViewGroup parent = (ViewGroup) viewParent;
        parent.removeView(this);
        super.destroy();
    }
    public void evaluateJS(String strJsFunction){
        this.evaluateJS(strJsFunction,null);
    }
    public void evaluateJS(String strJsFunction,ValueCallback valueCallback){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT&&valueCallback!=null) {
            this.evaluateJavascript("javascript:"+strJsFunction, valueCallback);
        } else {
            this.loadUrl("javascript:"+strJsFunction);
        }
    }

}
