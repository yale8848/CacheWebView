package ren.yale.android.cachewebview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.io.InputStream;

import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;

public class MainActivity extends Activity {

    private WebView mWebView;
    private static final String TAG = "CacheWebView";
    private String URL = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mWebView = findViewById(R.id.webview);

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                WebViewCacheInterceptorInst.getInstance().enableForce(isChecked);
            }
        });
        final String[] urls = getResources().getStringArray(R.array.urls);
        URL = urls[0];
        //URL=URL+"?r="+System.currentTimeMillis();
        Spinner spinner = (Spinner) findViewById(R.id.spnner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                URL = urls[position];
                //mInterceptor.loadUrl(mWebView,URL);
                //URL=URL+"?r="+System.currentTimeMillis();
                WebViewCacheInterceptorInst.getInstance().loadUrl(mWebView,URL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        initSettings();
        mWebView.setWebViewClient(new WebViewClient(){


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

           @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                WebViewCacheInterceptorInst.getInstance().loadUrl(mWebView,request.getUrl().toString());
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebViewCacheInterceptorInst.getInstance().loadUrl(mWebView,url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return  WebViewCacheInterceptorInst.getInstance().interceptRequest( request);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return  WebViewCacheInterceptorInst.getInstance().interceptRequest(url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                int code = error.getErrorCode();
                String resp = error.getDescription().toString();
                String url = request.getUrl().toString();
                super.onReceivedError(view, request, error);
            }
        });
        //WebViewCacheInterceptorInst.getInstance().initAssetsData();
        //WebViewCacheInterceptorInst.getInstance().loadUrl(mWebView,URL);

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

    public void cleanCache(View v){
        WebViewCacheInterceptorInst.getInstance().clearCache();
    }
    public void getCacheFile(View v){

        String url = "http://m.mm131.com/css/at.js";
        InputStream inputStream =  WebViewCacheInterceptorInst.getInstance().getCacheFile(url);
        if (inputStream!=null){

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
