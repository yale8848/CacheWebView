package ren.yale.android.cachewebview;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

import java.io.File;

import ren.yale.android.cachewebviewlib.ResourceInterceptor;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptor;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;
import ren.yale.android.cachewebviewlib.config.CacheExtensionConfig;

/**
 * Created by yale on 2017/9/27.
 */

public class App extends Application {
    private static final String CACHE_NAME = "cache_path";

    @Override
    public void onCreate() {
        super.onCreate();

        WebViewCacheInterceptor.Builder builder =  new WebViewCacheInterceptor.Builder(this);


        builder.setCachePath(new File(this.getCacheDir(),"cache_path_name"))//设置缓存路径，默认getCacheDir，名称CacheWebViewCache
                .setCacheSize(1024*1024*100)//设置缓存大小，默认100M
                .setConnectTimeoutSecond(20)//设置http请求链接超时，默认20秒
                .setReadTimeoutSecond(20);//设置http请求链接读取超时，默认20秒


        CacheExtensionConfig extension = new CacheExtensionConfig();
        extension.addExtension("json").removeExtension("swf");

        builder.setCacheExtensionConfig(extension);
        //builder.setAssetsDir("static");
        //builder.isAssetsSuffixMod(true);
        builder.setDebug(true);

        builder.setResourceInterceptor(new ResourceInterceptor() {
            @Override
            public boolean interceptor(String url) {
                return true;
            }
        });

        WebViewCacheInterceptorInst.getInstance().
                init(builder);

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);



    }
}
