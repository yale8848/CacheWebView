package ren.yale.android.cachewebview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.io.File;
import java.util.HashMap;

import ren.yale.android.cachewebviewlib.CacheInterceptor;
import ren.yale.android.cachewebviewlib.CacheStatus;
import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.WebViewCache;


public class MainActivity extends Activity {


    private String URL ="";
    private CacheWebView webview;
    private long mStart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TestWebView testWebView = (TestWebView) findViewById(R.id.webview);
        Spinner spinner = (Spinner) findViewById(R.id.spnner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] urls = getResources().getStringArray(R.array.urls);
                URL = urls[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        webview = testWebView.getDXHWebView();
        webview.setCacheStrategy(WebViewCache.CacheStrategy.FORCE);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mStart = System.currentTimeMillis();
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("CacheWebView",(System.currentTimeMillis()-mStart)+" "+url);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                CacheWebView v = (CacheWebView) view;
                view.loadUrl(url,getHeaderMap(url));
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);
       webview.setEnableCache(checkBox.isChecked());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                webview.setEnableCache(isChecked);
            }
        });
        webview.setCacheInterceptor(new CacheInterceptor() {

            public boolean canCache(String url) {
                return true;
            }
        });
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private HashMap getHeaderMap(String url){
        HashMap<String,String> map = new HashMap<>();
        map.put("key","value");
        return map;

    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_load:
                webview.stopLoading();
                webview.loadUrl(URL);
                break;
            case R.id.btn_preload:
                CacheWebView.servicePreload(MainActivity.this,URL,null);
                break;
            case R.id.btn_clearcache:
                clearCache();

                break;
            case R.id.btn_get_file:
                CacheStatus cacheStatus = webview.getWebViewCache().getCacheFile("https://m.baidu.com/static/search/baiduapp_icon.png");
                if (cacheStatus.isExist()){
                   File file = cacheStatus.getCacheFile();
                    String extension = cacheStatus.getExtension();
                }

                break;
        }
    }

    private void clearCache(){

        webview.clearCache();
    }

    @Override
    protected void onDestroy() {
        webview.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()){
            webview.goBack();
            return;
        }
        super.onBackPressed();
    }
}

