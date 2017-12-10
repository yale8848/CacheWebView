package ren.yale.android.cachewebviewlib;

import android.util.Log;

import ren.yale.android.cachewebviewlib.config.CacheConfig;

/**
 * Created by yale on 2017/9/15.
 */

 class CacheWebViewLog {
    private static final String TAG="CacheWebView";


    public static void d(String log){
        if (CacheConfig.getInstance().isDebug()){
            Log.d(TAG,log);
        }
    }
}
