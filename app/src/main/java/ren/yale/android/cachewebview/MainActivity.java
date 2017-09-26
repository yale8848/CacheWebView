package ren.yale.android.cachewebview;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.WebViewCache;


public class MainActivity extends AppCompatActivity {

    private static final String URL ="http://m.baidu.com";
    private CacheWebView webview;
    long mStart = 0;
    private static final String CACHE_NAME = "cahce_path";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = (CacheWebView) findViewById(R.id.webview);

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mStart = System.currentTimeMillis();
                view.getSettings().setLoadsImagesAutomatically(false);
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                view.getSettings().setLoadsImagesAutomatically(true);
                Log.d("CacheWebView",(System.currentTimeMillis()-mStart)+"");
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                CacheWebView v = (CacheWebView) view;
                if (url.startsWith("http")){
                    v.loadUrl(url,getHeaderMap(url));
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        try {
            File cacheFile = new File(this.getCacheDir(),CACHE_NAME);
            CacheWebView.getWebViewCache().init(this,cacheFile,1024*1024*100).
                    setCacheStrategy(WebViewCache.CacheStrategy.FORCE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Map getHeaderMap(String url){
        HashMap<String,String> map = new HashMap<>();
        map.put("aaa",url);
        return map;

    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_load:

                webview.loadUrl(URL,getHeaderMap(URL));
                break;
            case R.id.btn_preload:
                CacheWebView.preLoad(this,URL);
                break;
            case R.id.btn_clearcache:
                clearCache();

                break;
        }
    }

    private void clearCache(){

        CacheWebView.getWebViewCache().clean();
        webview.clearCache();
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

