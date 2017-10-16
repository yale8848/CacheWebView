package ren.yale.android.cachewebview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ren.yale.android.cachewebviewlib.CacheWebView;

public class Main2Activity extends AppCompatActivity {

    CacheWebView mCacheWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mCacheWebView = (CacheWebView) findViewById(R.id.webview);
    }
}
