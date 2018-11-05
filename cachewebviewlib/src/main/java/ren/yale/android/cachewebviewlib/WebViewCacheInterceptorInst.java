package ren.yale.android.cachewebviewlib;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by yale on 2018/7/16.
 */
public class WebViewCacheInterceptorInst implements WebViewRequestInterceptor {


    private static volatile  WebViewCacheInterceptorInst webViewCacheInterceptorInst;

    private WebViewRequestInterceptor mInterceptor;

    public void init(WebViewCacheInterceptor.Builder builder){
        if (builder!=null){
            mInterceptor =  builder.build();
        }
    }
    public static WebViewCacheInterceptorInst getInstance(){
        if (webViewCacheInterceptorInst==null){
            synchronized (WebViewCacheInterceptorInst.class){
                if (webViewCacheInterceptorInst == null){
                    webViewCacheInterceptorInst = new WebViewCacheInterceptorInst();
                }
            }
        }
        return webViewCacheInterceptorInst;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse interceptRequest( WebResourceRequest request) {
        if (mInterceptor==null){
            return null;
        }
        return mInterceptor.interceptRequest(request);
    }

    @Override
    public WebResourceResponse interceptRequest(String url) {
        if (mInterceptor==null){
            return null;
        }
        return mInterceptor.interceptRequest(url);
    }

    @Override
    public void loadUrl(WebView webView, String url) {
        if (mInterceptor==null){
            return ;
        }
        mInterceptor.loadUrl(webView,url);
    }

    @Override
    public void loadUrl(String url, String userAgent) {
        if (mInterceptor==null){
            return ;
        }
        mInterceptor.loadUrl(url,userAgent);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders, String userAgent) {
        if (mInterceptor==null){
            return ;
        }
        mInterceptor.loadUrl(url,additionalHttpHeaders,userAgent);
    }

    @Override
    public void loadUrl(WebView webView, String url, Map<String, String> additionalHttpHeaders) {
        if (mInterceptor==null){
            return ;
        }
        mInterceptor.loadUrl(webView,url,additionalHttpHeaders);
    }

    @Override
    public void clearCache() {
        if (mInterceptor==null){
            return ;
        }
        mInterceptor.clearCache();
    }

    @Override
    public void enableForce(boolean force) {
        if (mInterceptor==null){
            return ;
        }
        mInterceptor.enableForce(force);
    }

    @Override
    public InputStream getCacheFile(String url) {
        if (mInterceptor==null){
            return null;
        }
        return mInterceptor.getCacheFile(url);
    }

    @Override
    public void initAssetsData() {
        AssetsLoader.getInstance().initData();
    }

    @Override
    public File getCachePath() {
        if (mInterceptor==null){
            return null;
        }
        return mInterceptor.getCachePath();
    }
}
