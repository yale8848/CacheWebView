package ren.yale.android.cachewebviewlib;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by yale on 2017/9/15.
 */

final class CacheWebViewClient extends WebViewClient {


    public WebViewClient mCustomWebViewClient;
    private boolean mIsEnableCache = true;
    private boolean mIsBlockImageLoad = false;
    private WebViewCache.CacheStrategy mCacheStrategy = WebViewCache.CacheStrategy.NORMAL;
    private String mEncoding = "";
    private CacheInterceptor mCacheInterceptor;
    private Vector<String> mVisitVectorUrl = null;
    private String mUserAgent="";
    private HashMap<String,Map> mHeaderMaps;

    private WebViewCache mWebViewCache;

    public CacheWebViewClient(){
        mHeaderMaps = new HashMap<>();
        mVisitVectorUrl = new Vector<>();
    }

    public void setWebViewCache(WebViewCache webViewCache){
        mWebViewCache = webViewCache;
    }

    public void setCustomWebViewClient(WebViewClient webViewClient){
        mCustomWebViewClient = webViewClient;
    }
    public void setUserAgent(String agent){
        mUserAgent = agent;
    }
    public String getUserAgent(){
        return mUserAgent;
    }
    public void addHeaderMap(String url ,Map<String, String> additionalHttpHeaders){
        if(mHeaderMaps!=null&&additionalHttpHeaders!=null){
            mHeaderMaps.put(url,additionalHttpHeaders);
        }
    }

    public Map<String,String> getHeader(String url){
        if(mHeaderMaps!=null){
            return mHeaderMaps.get(url);
        }
        return null;
    }

    public void setEncoding(String encoding){
        mEncoding = encoding;
    }
    public void setCacheInterceptor(CacheInterceptor interceptor){
        mCacheInterceptor = interceptor;
    }
    public void addVisitUrl(String url){
        if (mVisitVectorUrl != null){
            if (!mVisitVectorUrl.contains(url)){
                mVisitVectorUrl.add(url);
            }
        }

    }

    public void clearLastUrl(){
        if (mVisitVectorUrl!=null&&mVisitVectorUrl.size()>0){
            mVisitVectorUrl.remove(mVisitVectorUrl.size()-1);
        }
    }

    public void clear(){
        if (mVisitVectorUrl!=null){
            mVisitVectorUrl.clear();
            mVisitVectorUrl = null;
        }
        if (mHeaderMaps!=null){
            mHeaderMaps.clear();
            mHeaderMaps = null;
        }

    }
    public String getOriginUrl(){
        String ou = "";
        if (mVisitVectorUrl == null){
            return ou;
        }
        try {
            ou =  mVisitVectorUrl.lastElement();
            URL url = new URL(ou);
            int port = url.getPort();
            ou=  url.getProtocol()+"://"+url.getHost()+(port==-1?"":":"+port);
        }catch (Exception e){
        }
        return ou;
    }
    public String getRefererUrl(){
        if (mVisitVectorUrl == null){
            return "";
        }
        try {
            if (mVisitVectorUrl.size()>0){
                return mVisitVectorUrl.get(mVisitVectorUrl.size()-1);
            }
        }catch (Exception e){
        }
        return "";
    }

    public String getHost(String u){
        String ou = "";
        try {
            URL url = new URL(u);
            ou=  url.getHost();
        }catch (Exception e){
        }
        return ou;
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (mCustomWebViewClient!=null){
            boolean ret =  mCustomWebViewClient.shouldOverrideUrlLoading(view,url);
            if (ret){
                return true;
            }
        }
        view.loadUrl(url);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

        if (mCustomWebViewClient!=null){
            boolean ret =  mCustomWebViewClient.shouldOverrideUrlLoading(view,request);
            if (ret){
                return true;
            }
        }
        view.loadUrl(request.getUrl().toString());
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (mIsBlockImageLoad){
            WebSettings webSettings = view.getSettings();
            webSettings.setBlockNetworkImage(true);
        }
        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.onPageStarted(view,url,favicon);
            return;
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (mIsBlockImageLoad){
            WebSettings webSettings = view.getSettings();
            webSettings.setBlockNetworkImage(false);
        }
        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.onPageFinished(view,url);
            return;
        }
        super.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.onLoadResource(view,url);
            return;
        }
        super.onLoadResource(view, url);
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        if (mCustomWebViewClient!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCustomWebViewClient.onPageCommitVisible(view,url);
                return;
            }
        }
        super.onPageCommitVisible(view, url);
    }

    public void setCacheStrategy(WebViewCache.CacheStrategy cacheStrategy){
        mCacheStrategy =cacheStrategy;
    }

    public void setBlockNetworkImage(boolean isBlock){
        mIsBlockImageLoad = isBlock;
    }

    public void setEnableCache(boolean enableCache){
        mIsEnableCache = enableCache;
    }
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebResourceResponse webResourceResponse = null;
        if (mCustomWebViewClient!=null){
            webResourceResponse =  mCustomWebViewClient.shouldInterceptRequest(view,url);
        }
        if (webResourceResponse != null){
            return webResourceResponse;

        }
        if (!mIsEnableCache){
            return null;
        }
        return mWebViewCache.getWebResourceResponse(this,url,mCacheStrategy,
                mEncoding,mCacheInterceptor);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        WebResourceResponse webResourceResponse = null;
        if (mCustomWebViewClient!=null){
            webResourceResponse =  mCustomWebViewClient.shouldInterceptRequest(view,request);
        }
        if (webResourceResponse != null){
            return webResourceResponse;
        }
        if (!mIsEnableCache){
            return null;
        }
        return mWebViewCache.getWebResourceResponse(this,request.getUrl().toString(),
                mCacheStrategy,mEncoding,mCacheInterceptor);
    }

    @Override
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {

        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
            return;
        }

        super.onTooManyRedirects(view, cancelMsg, continueMsg);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
            return;
        }

        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (mCustomWebViewClient!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCustomWebViewClient.onReceivedError(view, request, error);
            }
            return;
        }
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {

        if (mCustomWebViewClient!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCustomWebViewClient.onReceivedHttpError(view, request, errorResponse);
            }
            return;
        }

        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {

        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.onFormResubmission(view, dontResend, resend);
            return;
        }
        super.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {

        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.doUpdateVisitedHistory(view, url, isReload);
            return;
        }
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.onReceivedSslError( view,  handler,  error);
            return;
        }
        super.onReceivedSslError( view,  handler,  error);
    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (mCustomWebViewClient!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCustomWebViewClient.onReceivedClientCertRequest(view, request);
            }
            return;
        }
        super.onReceivedClientCertRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {

        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
            return;
        }

        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (mCustomWebViewClient!=null){
            return mCustomWebViewClient.shouldOverrideKeyEvent(view, event);
        }
        return super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.onUnhandledKeyEvent(view, event);
            return;
        }
        super.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.onScaleChanged(view, oldScale, newScale);
            return;
        }
        super.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        if (mCustomWebViewClient!=null){
            mCustomWebViewClient.onReceivedLoginRequest(view, realm, account, args);
            return;
        }
        super.onReceivedLoginRequest(view, realm, account, args);
    }
}
