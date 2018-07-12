package ren.yale.android.cachewebview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ren.yale.android.cachewebviewlib.utils.MimeTypeMapUtils;

public class Main3Activity extends Activity {

    private WebView mWebView;
    private CacheManager mCacheManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        mCacheManager = new CacheManager();
        mWebView = findViewById(R.id.webview);
       // mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebViewClient(new WebViewClient(){


            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                //return null;
                //return super.shouldInterceptRequest(view,url);
                return mCacheManager.InterceptRequest(view,url);
            }
        });
        mWebView.loadUrl("https://lftresource.oss-cn-qingdao.aliyuncs.com/test/yale/test.html");
    }

    class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Response originResponse = chain.proceed(chain.request());
            return originResponse.newBuilder().removeHeader("pragma").removeHeader("Cache-Control")
                    .header("Cache-Control","max-age=60").build();
        }
    }
    class CacheManager {
        OkHttpClient client = null;

        public CacheManager(){

            File cacheFile = new File(getExternalCacheDir().toString(),"CacheWebViewCache");

            int cacheSize = 10 * 1024 * 1024;

            final Cache cache = new Cache(cacheFile,cacheSize);

            client = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();
        }

        private Map<String,String> multimapToSingle(Map<String, List<String>> maps){

            StringBuilder sb = new StringBuilder();
            Map<String,String> map = new HashMap<>();
            for (Map.Entry<String, List<String>> entry: maps.entrySet()) {
                List<String> values = entry.getValue();
                sb.delete(0,sb.length());
                if (values!=null&&values.size()>0){
                    for (String v:values) {
                        sb.append(v);
                        sb.append(";");
                    }
                }
                if (sb.length()>0){
                    sb.deleteCharAt(sb.length()-1);
                }
                map.put(entry.getKey(),sb.toString());
            }
            return map;
        }

        public WebResourceResponse InterceptRequest(WebView view, String url){


            Request request = new Request.Builder()
                    .url(url)

                    .build();

            String extension = MimeTypeMapUtils.getFileExtensionFromUrl(url);
            String mimeType = MimeTypeMapUtils.getMimeTypeFromExtension(extension);
            try {
                Response response = client.newCall(request).execute();
                WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType,"",response.body().byteStream());
               if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                   webResourceResponse.setResponseHeaders(multimapToSingle(response.headers().toMultimap()));
               }
               return webResourceResponse;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
