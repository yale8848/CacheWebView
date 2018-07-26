package ren.yale.android.cachewebview;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;

import java.util.Map;

/**
 * Created by yale on 2018/7/26.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class WebResourceRequestAdapter implements android.webkit.WebResourceRequest {

    private com.tencent.smtt.export.external.interfaces.WebResourceRequest mWebResourceRequest;

    private WebResourceRequestAdapter(com.tencent.smtt.export.external.interfaces.WebResourceRequest x5Request){
        mWebResourceRequest = x5Request;
    }

    public static WebResourceRequestAdapter adapter(com.tencent.smtt.export.external.interfaces.WebResourceRequest x5Request){
        return new WebResourceRequestAdapter(x5Request);
    }

    @Override
    public Uri getUrl() {
        return mWebResourceRequest.getUrl();
    }

    @Override
    public boolean isForMainFrame() {
        return mWebResourceRequest.isForMainFrame();
    }

    @Override
    public boolean isRedirect() {
        return mWebResourceRequest.isRedirect();
    }

    @Override
    public boolean hasGesture() {
        return mWebResourceRequest.hasGesture();
    }

    @Override
    public String getMethod() {
        return mWebResourceRequest.getMethod();
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        return mWebResourceRequest.getRequestHeaders();
    }
}
