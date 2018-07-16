package ren.yale.android.cachewebview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.io.File;

import ren.yale.android.cachewebviewlib.WebViewCacheInterceptor;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;
import ren.yale.android.cachewebviewlib.WebViewRequestInterceptor;

public class Main3Activity extends Activity {

    private WebView mWebView;
    private static final String TAG = "CacheWebView";
    private String URL = "";


    private WebViewRequestInterceptor mInterceptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        //mInterceptor = new WebViewCacheInterceptor.Builder(this).build();

        WebViewCacheInterceptorInst.getInstance().init(new WebViewCacheInterceptor.Builder(this));


        mWebView = findViewById(R.id.webview);


        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //mInterceptor.enableForce(isChecked);
                WebViewCacheInterceptorInst.getInstance().enableForce(isChecked);
            }
        });
        final String[] urls = getResources().getStringArray(R.array.urls);
        URL = urls[0];
        Spinner spinner = (Spinner) findViewById(R.id.spnner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                URL = urls[position];
                //mInterceptor.loadUrl(mWebView,URL);
                WebViewCacheInterceptorInst.getInstance().loadUrl(mWebView,URL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        initSettings();
        mWebView.setWebViewClient(new WebViewClient(){


            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                //mInterceptor.loadUrl(mWebView,request.getUrl().toString());

                WebViewCacheInterceptorInst.getInstance().loadUrl(mWebView,request.getUrl().toString());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //mInterceptor.loadUrl(mWebView,url);
                WebViewCacheInterceptorInst.getInstance().loadUrl(mWebView,url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return  WebViewCacheInterceptorInst.getInstance().interceptRequest(view, request);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return  WebViewCacheInterceptorInst.getInstance().interceptRequest(view,url);
            }
        });
        WebViewCacheInterceptorInst.getInstance().loadUrl(mWebView,URL);
    }

    private void initSettings() {
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        webSettings.setDefaultTextEncodingName("UTF-8");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(
                    WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
    }

    public void getCacheFile(View v){
        //http://m.mm131.com/css/at.js

        String url = "https://www.ranwena.com/scripts/header.js";
        //String url = "http://j.xnojy.com:8080/cpv/bd/sdk/hj3.gif";
        File df = new File(WebViewCacheInterceptorInst.getInstance().getCachePath(),"aaa.js");
        boolean find =  WebViewCacheInterceptorInst.getInstance().getCacheFile(url,df);
        if (find){

        }

    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
            return;
        }
       // mInterceptor.clearCache();
        super.onBackPressed();
    }
}
