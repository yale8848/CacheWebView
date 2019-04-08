package ren.yale.android.cachewebviewlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ren.yale.android.cachewebviewlib.config.CacheExtensionConfig;
import ren.yale.android.cachewebviewlib.utils.FileUtil;
import ren.yale.android.cachewebviewlib.utils.MimeTypeMapUtils;
import ren.yale.android.cachewebviewlib.utils.NetUtils;
import ren.yale.android.cachewebviewlib.utils.OKHttpFile;


/**
 * Created by yale on 2018/7/13.
 */
public class WebViewCacheInterceptor implements WebViewRequestInterceptor {

    private File mCacheFile;
    private long mCacheSize;
    private long mConnectTimeout;
    private long mReadTimeout;
    private CacheExtensionConfig mCacheExtensionConfig;
    private Context mContext;
    private boolean mDebug;
    private CacheType mCacheType;
    private String mAssetsDir = null;
    private boolean mTrustAllHostname = false;
    private SSLSocketFactory mSSLSocketFactory = null;
    private X509TrustManager mX509TrustManager = null;
    private Dns mDns=null;
    private ResourceInterceptor mResourceInterceptor;
    private boolean mIsSuffixMod = false;

    //==============
    private OkHttpClient mHttpClient = null;
    private String mOrigin = "";
    private String mReferer = "";
    private String mUserAgent = "";
    public static final String KEY_CACHE = "WebResourceInterceptor-Key-Cache";


    public WebViewCacheInterceptor(Builder builder) {

        this.mCacheExtensionConfig = builder.mCacheExtensionConfig;
        this.mCacheFile = builder.mCacheFile;
        this.mCacheSize = builder.mCacheSize;
        this.mCacheType = builder.mCacheType;
        this.mConnectTimeout = builder.mConnectTimeout;
        this.mReadTimeout = builder.mReadTimeout;
        this.mContext = builder.mContext;
        this.mDebug = builder.mDebug;
        this.mAssetsDir = builder.mAssetsDir;
        this.mX509TrustManager = builder.mX509TrustManager;
        this.mSSLSocketFactory = builder.mSSLSocketFactory;
        this.mTrustAllHostname = builder.mTrustAllHostname;
        this.mResourceInterceptor = builder.mResourceInterceptor;
        this.mIsSuffixMod = builder.mIsSuffixMod;
        this.mDns = builder.mDns;

        initHttpClient();
        if (isEnableAssets()) {
            initAssetsLoader();
        }
    }

    private boolean isEnableAssets() {
        return mAssetsDir != null;
    }

    private void initAssetsLoader() {
        AssetsLoader.getInstance().init(mContext).setDir(mAssetsDir).isAssetsSuffixMod(mIsSuffixMod);
    }

    private void initHttpClient() {

        final Cache cache = new Cache(mCacheFile, mCacheSize);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                .readTimeout(mReadTimeout, TimeUnit.SECONDS)
                .addNetworkInterceptor(new HttpCacheInterceptor());
        if (mTrustAllHostname) {
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }
        if (mSSLSocketFactory != null && mX509TrustManager != null) {
            builder.sslSocketFactory(mSSLSocketFactory, mX509TrustManager);
        }
        if(mDns!=null){
            builder.dns(mDns);
        }
        mHttpClient = builder.build();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse interceptRequest(WebResourceRequest request) {
        return interceptRequest(request.getUrl().toString(), request.getRequestHeaders());
    }

    private Map<String, String> buildHeaders() {

        Map<String, String> headers = new HashMap<String, String>();
        if (!TextUtils.isEmpty(mOrigin)) {
            headers.put("Origin", mOrigin);
        }
        if (!TextUtils.isEmpty(mReferer)) {
            headers.put("Referer", mReferer);
        }
        if (!TextUtils.isEmpty(mUserAgent)) {
            headers.put("User-Agent", mUserAgent);
        }
        return headers;
    }

    @Override
    public WebResourceResponse interceptRequest(String url) {
        return interceptRequest(url, buildHeaders());
    }

    private boolean checkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        //okhttp only deal with http[s]
        if (!url.startsWith("http")) {
            return false;
        }

        if (mResourceInterceptor != null && !mResourceInterceptor.interceptor(url)) {
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

    @Override
    public void loadUrl(WebView webView, String url) {
        if (!isValidUrl(url)) {
            return;
        }
        webView.loadUrl(url);
        mReferer = webView.getUrl();
        mOrigin = NetUtils.getOriginUrl(mReferer);
        mUserAgent = webView.getSettings().getUserAgentString();
    }

    @Override
    public void loadUrl(String url, String userAgent) {
        if (!isValidUrl(url)) {
            return;
        }
        mReferer = url;
        mOrigin = NetUtils.getOriginUrl(mReferer);
        mUserAgent = userAgent;
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders, String userAgent) {
        if (!isValidUrl(url)) {
            return;
        }
        mReferer = url;
        mOrigin = NetUtils.getOriginUrl(mReferer);
        mUserAgent = userAgent;
    }

    @Override
    public void loadUrl(WebView webView, String url, Map<String, String> additionalHttpHeaders) {
        if (!isValidUrl(url)) {
            return;
        }
        webView.loadUrl(url, additionalHttpHeaders);
        mReferer = webView.getUrl();
        mOrigin = NetUtils.getOriginUrl(mReferer);
        mUserAgent = webView.getSettings().getUserAgentString();
    }

    @Override
    public void clearCache() {
        FileUtil.deleteDirs(mCacheFile.getAbsolutePath(), false);
        AssetsLoader.getInstance().clear();

    }

    @Override
    public void enableForce(boolean force) {
        if (force) {
            mCacheType = CacheType.FORCE;
        } else {
            mCacheType = CacheType.NORMAL;
        }
    }

    @Override
    public InputStream getCacheFile(String url) {
        return OKHttpFile.getCacheFile(mCacheFile, url);
    }

    @Override
    public void initAssetsData() {
        AssetsLoader.getInstance().initData();
    }


    @Override
    public File getCachePath() {
        return mCacheFile;
    }

    public void addHeader(Request.Builder reqBuilder, Map<String, String> headers) {

        if (headers == null) {
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            reqBuilder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    private WebResourceResponse interceptRequest(String url, Map<String, String> headers) {

        if (mCacheType == CacheType.NORMAL) {
            return null;
        }
        if (!checkUrl(url)) {
            return null;
        }

        if (isEnableAssets()) {
            InputStream inputStream = AssetsLoader.getInstance().getResByUrl(url);
            if (inputStream != null) {
                CacheWebViewLog.d(String.format("from assets: %s", url), mDebug);
                String mimeType = MimeTypeMapUtils.getMimeTypeFromUrl(url);
                WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", inputStream);
                return webResourceResponse;
            }
        }
        try {

            Request.Builder reqBuilder = new Request.Builder()
                    .url(url);

            String extension = MimeTypeMapUtils.getFileExtensionFromUrl(url);

            if (mCacheExtensionConfig.isHtml(extension)) {
                headers.put(KEY_CACHE, mCacheType.ordinal() + "");
            }
            addHeader(reqBuilder, headers);

            if (!NetUtils.isConnected(mContext)) {
                reqBuilder.cacheControl(CacheControl.FORCE_CACHE);
            }
            Request request = reqBuilder.build();
            Response response = mHttpClient.newCall(request).execute();
            Response cacheRes = response.cacheResponse();
            if (cacheRes != null) {
                CacheWebViewLog.d(String.format("from cache: %s", url), mDebug);
            } else {
                CacheWebViewLog.d(String.format("from server: %s", url), mDebug);
            }
            String mimeType = MimeTypeMapUtils.getMimeTypeFromUrl(url);
            WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", response.body().byteStream());
            if (response.code() == 504 && !NetUtils.isConnected(mContext)){
                return null;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String message = response.message();
                if (TextUtils.isEmpty(message)) {
                    message = "OK";
                }
                try {
                    webResourceResponse.setStatusCodeAndReasonPhrase(response.code(), message);
                } catch (Exception e) {
                    return null;
                }
                webResourceResponse.setResponseHeaders(NetUtils.multimapToSingle(response.headers().toMultimap()));
            }
            return webResourceResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Builder {

        private File mCacheFile;
        private long mCacheSize = 100 * 1024 * 1024;
        private long mConnectTimeout = 20;
        private long mReadTimeout = 20;
        private CacheExtensionConfig mCacheExtensionConfig;
        private Context mContext;
        private boolean mDebug = true;
        private CacheType mCacheType = CacheType.FORCE;


        private boolean mTrustAllHostname = false;
        private SSLSocketFactory mSSLSocketFactory = null;
        private X509TrustManager mX509TrustManager = null;
        private ResourceInterceptor mResourceInterceptor;

        private String mAssetsDir = null;
        private boolean mIsSuffixMod = false;
        private Dns mDns=null;
        public Builder(Context context) {

            mContext = context;
            mCacheFile = new File(context.getCacheDir().toString(), "CacheWebViewCache");
            mCacheExtensionConfig = new CacheExtensionConfig();
        }

        public void setResourceInterceptor(ResourceInterceptor resourceInterceptor) {
            mResourceInterceptor = resourceInterceptor;
        }

        public Builder setTrustAllHostname() {
            mTrustAllHostname = true;
            return this;
        }

        public Builder setSSLSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
            if (sslSocketFactory != null && trustManager != null) {
                mSSLSocketFactory = sslSocketFactory;
                mX509TrustManager = trustManager;
            }
            return this;
        }

        public Builder setCachePath(File file) {
            if (file != null) {
                mCacheFile = file;
            }
            return this;
        }

        public Builder setCacheSize(long cacheSize) {
            if (cacheSize > 1024) {
                mCacheSize = cacheSize;
            }
            return this;
        }

        public Builder setReadTimeoutSecond(long time) {
            if (time >= 0) {
                mReadTimeout = time;
            }
            return this;
        }

        public Builder setConnectTimeoutSecond(long time) {
            if (time >= 0) {
                mConnectTimeout = time;
            }

            return this;
        }

        public Builder setCacheExtensionConfig(CacheExtensionConfig config) {
            if (config != null) {
                mCacheExtensionConfig = config;
            }
            return this;
        }

        public Builder setDebug(boolean debug) {
            mDebug = debug;
            return this;
        }

        public Builder setCacheType(CacheType cacheType) {
            mCacheType = cacheType;
            return this;
        }

        public Builder isAssetsSuffixMod(boolean suffixMod) {
            this.mIsSuffixMod = suffixMod;
            return this;
        }

        public Builder setAssetsDir(String dir) {
            if (dir != null) {
                mAssetsDir = dir;
            }
            return this;
        }

        public void setDns(Dns dns) {
            this.mDns = dns;
        }

        public WebViewRequestInterceptor build() {
            return new WebViewCacheInterceptor(this);
        }

    }

    boolean isValidUrl(String url) {
        return URLUtil.isValidUrl(url);
    }
}
