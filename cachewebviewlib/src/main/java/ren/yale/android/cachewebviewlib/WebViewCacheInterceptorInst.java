package ren.yale.android.cachewebviewlib;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.File;

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
    public WebResourceResponse interceptRequest(WebView view, WebResourceRequest request) {
        if (mInterceptor==null){
            return null;
        }
        return mInterceptor.interceptRequest(view,request);
    }

    @Override
    public WebResourceResponse interceptRequest(WebView view, String url) {
        if (mInterceptor==null){
            return null;
        }
        return mInterceptor.interceptRequest(view,url);
    }

    @Override
    public void loadUrl(WebView webView, String url) {
        if (mInterceptor==null){
            return ;
        }
        mInterceptor.loadUrl(webView,url);
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
    public boolean getCacheFile(String url, File desPath) {
        if (mInterceptor==null){
            return false;
        }
        return mInterceptor.getCacheFile(url, desPath);
    }

    @Override
    public File getCachePath() {
        if (mInterceptor==null){
            return null;
        }
        return mInterceptor.getCachePath();
    }
}
