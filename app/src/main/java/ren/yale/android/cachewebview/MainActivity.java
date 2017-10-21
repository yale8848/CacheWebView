package ren.yale.android.cachewebview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.WebViewCache;


public class MainActivity extends AppCompatActivity {

    private static final String URL ="http://www.baidu.com";
    private static final String URL1 ="http://m.mm131.com/xinggan/3320_3.html";
    private CacheWebView webview;
    private long mStart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TestWebView testWebView = (TestWebView) findViewById(R.id.webview);
        webview = testWebView.getDXHWebView();
        webview.setCacheStrategy(WebViewCache.CacheStrategy.FORCE);
        webview.setEnableCache(true);
        webview.setBlockNetworkImage(false);

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mStart = System.currentTimeMillis();
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("CacheWebView",(System.currentTimeMillis()-mStart)+"");
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                CacheWebView v = (CacheWebView) view;
                if (url.startsWith("http")){
                    view.loadUrl(url,getHeaderMap(url));
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        //webview.setEnableCache(false);
        //webview.setUserAgent("Android");
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
       // CacheWebView.cacheWebView(MainActivity.this)
       //         .loadUrl(URL);
    }

    private Map getHeaderMap(String url){
        HashMap<String,String> map = new HashMap<>();
        map.put("aaa",url);
        return map;

    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_load:

                webview.loadUrl(URL);
                break;
            case R.id.btn_preload:
                CacheWebView.cacheWebView(this).loadUrl(URL);
                break;
            case R.id.btn_clearcache:
                clearCache();

                break;
            case R.id.btn_start2:
                startActivity(new Intent(MainActivity.this,Main2Activity.class));
                break;
        }
    }

    private void clearCache(){

        CacheWebView.getWebViewCache().clean();
    }

    @Override
    protected void onDestroy() {
        webview.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

