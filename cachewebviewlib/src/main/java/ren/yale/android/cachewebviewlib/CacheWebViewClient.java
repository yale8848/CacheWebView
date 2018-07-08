package ren.yale.android.cachewebviewlib;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by yale on 2017/9/15.
 */

final class CacheWebViewClient extends DelegateWebViewClient {

    private boolean mIsEnableCache = true;
    private boolean mIsBlockImageLoad = false;
    private WebViewCache.CacheStrategy mCacheStrategy = WebViewCache.CacheStrategy.NORMAL;
    private String mEncoding = "";
    private CacheInterceptor mCacheInterceptor;
    private Vector<String> mVisitVectorUrl = null;
    private String mUserAgent = "";
    private HashMap<String, Map> mHeaderMaps;

    private WebViewCache mWebViewCache;

    public CacheWebViewClient() {
        mHeaderMaps = new HashMap<>();
        mVisitVectorUrl = new Vector<>();
    }

    public void setWebViewCache(WebViewCache webViewCache) {
        mWebViewCache = webViewCache;
    }

    public void setUserAgent(String agent) {
        mUserAgent = agent;
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    public void addHeaderMap(String url, Map<String, String> additionalHttpHeaders) {
        if (mHeaderMaps != null && additionalHttpHeaders != null) {
            mHeaderMaps.put(url, additionalHttpHeaders);
        }
    }

    public Map<String, String> getHeader(String url) {
        if (mHeaderMaps != null) {
            return mHeaderMaps.get(url);
        }
        return null;
    }

    public void setEncoding(String encoding) {
        mEncoding = encoding;
    }

    public void setCacheInterceptor(CacheInterceptor interceptor) {
        mCacheInterceptor = interceptor;
    }

    public void addVisitUrl(String url) {
        if (mVisitVectorUrl != null) {
            if (!mVisitVectorUrl.contains(url)) {
                mVisitVectorUrl.add(url);
            }
        }
    }

    public void clearLastUrl() {
        if (mVisitVectorUrl != null && mVisitVectorUrl.size() > 0) {
            mVisitVectorUrl.remove(mVisitVectorUrl.size() - 1);
        }
    }

    public void clear() {
        if (mVisitVectorUrl != null) {
            mVisitVectorUrl.clear();
            mVisitVectorUrl = null;
        }
        if (mHeaderMaps != null) {
            mHeaderMaps.clear();
            mHeaderMaps = null;
        }
    }

    public String getOriginUrl() {
        String ou = "";
        if (mVisitVectorUrl == null) {
            return ou;
        }
        try {
            ou = mVisitVectorUrl.lastElement();
            URL url = new URL(ou);
            int port = url.getPort();
            ou = url.getProtocol() + "://" + url.getHost() + (port == -1 ? "" : ":" + port);
        } catch (Exception e) {
        }
        return ou;
    }

    public String getRefererUrl() {
        if (mVisitVectorUrl == null) {
            return "";
        }
        try {
            if (mVisitVectorUrl.size() > 0) {
                return mVisitVectorUrl.get(mVisitVectorUrl.size() - 1);
            }
        } catch (Exception e) {
        }
        return "";
    }

    public String getHost(String u) {
        String ou = "";
        try {
            URL url = new URL(u);
            ou = url.getHost();
        } catch (Exception e) {
        }
        return ou;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (super.shouldOverrideUrlLoading(view, url)) {
            return true;
        }
        view.loadUrl(url);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (super.shouldOverrideUrlLoading(view, request)) {
            return true;
        }
        view.loadUrl(request.getUrl().toString());
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (mIsBlockImageLoad) {
            WebSettings webSettings = view.getSettings();
            webSettings.setBlockNetworkImage(true);
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (mIsBlockImageLoad) {
            WebSettings webSettings = view.getSettings();
            webSettings.setBlockNetworkImage(false);
        }
        super.onPageFinished(view, url);
    }

    public void setCacheStrategy(WebViewCache.CacheStrategy cacheStrategy) {
        mCacheStrategy = cacheStrategy;
    }

    public void setBlockNetworkImage(boolean isBlock) {
        mIsBlockImageLoad = isBlock;
    }

    public void setEnableCache(boolean enableCache) {
        mIsEnableCache = enableCache;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebResourceResponse webResourceResponse = super.shouldInterceptRequest(view, url);

        if (webResourceResponse != null) {
            return webResourceResponse;
        }
        if (!mIsEnableCache) {
            return null;
        }
        return mWebViewCache.getWebResourceResponse(this, url, mCacheStrategy, mEncoding, mCacheInterceptor);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        WebResourceResponse webResourceResponse = super.shouldInterceptRequest(view, request);
        if (webResourceResponse != null) {
            return webResourceResponse;
        }
        if (!mIsEnableCache) {
            return null;
        }
        return mWebViewCache.getWebResourceResponse(this, request.getUrl().toString(), mCacheStrategy, mEncoding, mCacheInterceptor);
    }
}
