package ren.yale.android.cachewebview;

import android.app.Application;

import java.io.File;

import ren.yale.android.cachewebviewlib.CacheWebView;

/**
 * Created by yale on 2017/9/27.
 */

public class App extends Application {
    private static final String CACHE_NAME = "cache_path";

    @Override
    public void onCreate() {
        super.onCreate();

        File cacheFile = new File(this.getCacheDir(), CACHE_NAME);
        CacheWebView.getCacheConfig().init(this, cacheFile.getAbsolutePath(), 1024 * 1024 * 100, 1024 * 1024 * 10)
                .enableDebug(true);


    }
}
