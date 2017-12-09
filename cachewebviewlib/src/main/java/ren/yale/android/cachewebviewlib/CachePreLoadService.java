package ren.yale.android.cachewebviewlib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Map;

import ren.yale.android.cachewebviewlib.utils.NetworkUtils;

/**
 * Created by yale on 2017/10/27.
 */

public class CachePreLoadService extends Service {
    public static final String KEY_URL = "preload_url_key";
    public static final String KEY_URL_HEADER = "preload_url_key_header";
    private boolean mLastFinish = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null){
            return super.onStartCommand(intent, flags, startId);
        }

        if (!NetworkUtils.isConnected(this.getApplicationContext())){
            return super.onStartCommand(intent, flags, startId);
        }
        String url  = intent.getStringExtra(KEY_URL);
        if (!TextUtils.isEmpty(url)&&mLastFinish){
            mLastFinish = false;
            CacheWebView cacheWebView = new CacheWebView(this.getApplicationContext());
            cacheWebView.setCacheStrategy(WebViewCache.CacheStrategy.FORCE);
            Map header = null;
            try {
                header = (Map) intent.getSerializableExtra(KEY_URL_HEADER);
            }catch (Exception e){
            }
            cacheWebView.loadUrl(url,header);
            cacheWebView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    mLastFinish = true;
                }
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    mLastFinish = true;
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    mLastFinish = true;
                }
            });
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
