package ren.yale.android.cachewebview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.WebViewCache;


public class MainActivity extends AppCompatActivity {

    private static final String URL ="http://121.42.232.14:5600/converter/public/gs/index.html";
    //private static final String URL ="http://lftbjb.52fdw.com:9058/LFT-EditingSystem/page/knowdic/knowdic-view.html?uid=1:101:701:[699]";
    //private static final String URL = "https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fr=&hs=0&xthttps=111121&sf=1&fmq=&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word=%E5%B0%8F%E6%B8%85%E6%96%B0&oq=%E5%B0%8F%E6%B8%85%E6%96%B0&rsp=-1";
    //private static final String URL ="https://lftresource.oss-cn-qingdao.aliyuncs.com/test/index1.html";
    //private static final String URL ="http://www.baidu.com";
    CacheWebView webview;
    long mStart = 0;
    private static final String CACHE_NAME = "FastWebView68";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = (CacheWebView) findViewById(R.id.webview);

        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mStart = System.currentTimeMillis();
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                CacheWebView v = (CacheWebView) view;
                v.loadUrl(url,getHeaderMap(url));
                return true;
            }
        });

        try {
            File cacheFile = new File(webview.getContext().getCacheDir(),CACHE_NAME);
            CacheWebView.getWebViewCache().init(this,cacheFile,1024*1024*10).
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
        webview.clearCache();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

