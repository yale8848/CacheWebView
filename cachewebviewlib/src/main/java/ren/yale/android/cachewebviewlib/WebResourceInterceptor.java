package ren.yale.android.cachewebviewlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ren.yale.android.cachewebviewlib.config.CacheExtensionConfig;
import ren.yale.android.cachewebviewlib.utils.MimeTypeMapUtils;
import ren.yale.android.cachewebviewlib.utils.NetUtils;

/**
 * Created by yale on 2018/7/13.
 */
public class WebResourceInterceptor {

    private static volatile WebResourceInterceptor mWebRes = null;

    private CacheExtensionConfig mCacheExtensionConfig;
    private Context mContext;
    private OkHttpClient client = null;
    private String mOrigin = "";
    private String mReferer="";
    private String mUserAgent="";

    private CacheType mCacheType = CacheType.FORCE;
    private static final String KEY_CACHE="WebResourceInterceptor-Key-Cache";

    public WebResourceInterceptor(){

    }
    public void init(Context context){
        mContext = context;

        File cacheFile = new File(context.getCacheDir().toString(),"CacheWebViewCache");

        int cacheSize = 10 * 1024 * 1024;

        final Cache cache = new Cache(cacheFile,cacheSize);

        client = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addNetworkInterceptor(new HttpCacheInterceptor())
                .build();

        mCacheExtensionConfig = new CacheExtensionConfig();
    }
    class HttpCacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            String cache = request.header(KEY_CACHE);
            Response originResponse = chain.proceed(request);
            if (cache == CacheType.NORMAL.ordinal()+""){
               return originResponse;
            }
            return originResponse.newBuilder().removeHeader("pragma").removeHeader("Cache-Control")
                    .header("Cache-Control","max-age=3153600000").build();
        }
    }
    public static WebResourceInterceptor getInstance(){

        if (mWebRes == null){
            synchronized (WebResourceInterceptor.class){
                if (mWebRes == null){
                    mWebRes = new WebResourceInterceptor();
                }
            }
        }
        return mWebRes;
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

    public WebResourceInterceptor setCacheType(CacheType cacheType){
        mCacheType = cacheType;
        return this;
    }
    public void loadUrl(WebView webView ,String url){
        if (!url.startsWith("http")){
            return;
        }
        webView.loadUrl(url);
        mReferer = webView.getUrl();
        mOrigin = getOriginUrl();
        mUserAgent = webView.getSettings().getUserAgentString();

    }

    public String getOriginUrl() {
        String ou = mReferer;
        if (TextUtils.isEmpty(ou)) {
            return "";
        }
        try {
            URL url = new URL(ou);
            int port = url.getPort();
            ou = url.getProtocol() + "://" + url.getHost() + (port == -1 ? "" : ":" + port);
        } catch (Exception e) {
        }
        return ou;
    }
    private boolean checkUrl(String url){
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (!url.startsWith("http")) {
            return false;
        }
        String extension = MimeTypeMapUtils.getFileExtensionFromUrl(url);


        if (TextUtils.isEmpty(extension)) {
            return false;
        }
        if (mCacheExtensionConfig.isMedia(extension)) {
            return false;
        }
        if (!mCacheExtensionConfig.canCache(extension)) {
            return false;
        }

        return true;
    }

    public void addHeader(Request.Builder reqBuilder,Map<String, String> headers){

        if (headers==null){
            return;
        }
        for (Map.Entry<String,String> entry:headers.entrySet()){
            reqBuilder.addHeader(entry.getKey(),entry.getValue());
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WebResourceResponse interceptRequest(WebView view, WebResourceRequest request){
        return interceptRequest(view,request.getUrl().toString(),request.getRequestHeaders());
    }
    public WebResourceResponse interceptRequest(WebView view, String url){
        return interceptRequest(view,url,buildHeaders());
    }

    private Map<String, String> buildHeaders(){

            Map<String, String> headers  = new HashMap<String, String>();
            if (!TextUtils.isEmpty(mOrigin)){
                headers.put("Origin",mOrigin);
            }
            if (!TextUtils.isEmpty(mReferer)){
                headers.put("Referer",mReferer);
            }
            if (!TextUtils.isEmpty(mUserAgent)){
                headers.put("User-Agent",mUserAgent);
            }
        return headers;
    }

    private WebResourceResponse interceptRequest(WebView view, String url, Map<String, String> headers){

        if(mCacheType==CacheType.NORMAL){
            return null;
        }
        if (!checkUrl(url)){
            return null;
        }
        try {

            Request.Builder reqBuilder = new Request.Builder()
                    .url(url);
            String extension = MimeTypeMapUtils.getFileExtensionFromUrl(url);

            if (mCacheExtensionConfig.isHtml(extension)){
                headers.put(KEY_CACHE,mCacheType.ordinal()+"");
            }
            addHeader(reqBuilder,headers);

            if (!NetUtils.isConnected(mContext)) {
                reqBuilder.cacheControl(CacheControl.FORCE_CACHE);
            }
            Request request =  reqBuilder.build();
            Response response = client.newCall(request).execute();
            Response cacheRes = response.cacheResponse();
            if (cacheRes!=null){
                CacheWebViewLog.d("from cache : "+url);
            }else{
                CacheWebViewLog.d(url);
            }
            String mimeType = MimeTypeMapUtils.getMimeTypeFromUrl(url);
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
