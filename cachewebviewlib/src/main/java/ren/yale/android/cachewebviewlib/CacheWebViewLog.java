package ren.yale.android.cachewebviewlib;

import android.util.Log;

/**
 * Created by yale on 2017/9/15.
 */

class CacheWebViewLog {
    private static final String TAG = "CacheWebView";


    public static void d(String log) {
        Log.d(TAG, log);
    }
    public static void d(String log,boolean show) {
        if (show){
            d(log);
        }
    }
}
