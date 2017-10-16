package ren.yale.android.cachewebview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.Collections;

import ren.yale.android.cachewebviewlib.CacheWebView;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG="CacheWebView1";
    CacheWebView mCacheWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mCacheWebView = (CacheWebView) findViewById(R.id.webview);
        mCacheWebView.loadUrl("http://m.baidu.com");
        mCacheWebView.clearCache();
        mCacheWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG,"===load "+url);
                view.loadUrl(url);
                return true;
            }
        });

    }

    public void goBack(View v){
        if (mCacheWebView.canGoBack()){
            mCacheWebView.goBack();
            Log.d(TAG,"+++"+mCacheWebView.getUrl());
            Collections.synchronizedList(new ArrayList());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCacheWebView.destroy();
    }
}
