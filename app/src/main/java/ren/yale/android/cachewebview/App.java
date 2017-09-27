package ren.yale.android.cachewebview;

import android.app.Application;

import java.io.File;

import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.WebViewCache;

/**
 * Created by yale on 2017/9/27.
 */

public class App extends Application {
    private static final String CACHE_NAME = "cahce_path";
    @Override
    public void onCreate() {
        super.onCreate();

        File cacheFile = new File(this.getCacheDir(),CACHE_NAME);
        CacheWebView.getWebViewCache().init(this,cacheFile,1024*1024*100).
                setCacheStrategy(WebViewCache.CacheStrategy.FORCE);


    }
}
