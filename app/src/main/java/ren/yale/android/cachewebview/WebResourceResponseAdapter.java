package ren.yale.android.cachewebview;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by yale on 2018/7/26.
 */
public class WebResourceResponseAdapter extends com.tencent.smtt.export.external.interfaces.WebResourceResponse {

    private android.webkit.WebResourceResponse mWebResourceResponse;

    private WebResourceResponseAdapter(android.webkit.WebResourceResponse webResourceResponse){
        mWebResourceResponse = webResourceResponse;
    }

    public static WebResourceResponseAdapter adapter(android.webkit.WebResourceResponse webResourceResponse){
        if (webResourceResponse == null){
            return null;
        }
        return new WebResourceResponseAdapter(webResourceResponse);

    }

    @Override
    public String getMimeType() {
        return mWebResourceResponse.getMimeType();
    }

    @Override
    public InputStream getData() {
        return mWebResourceResponse.getData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int getStatusCode() {
        return mWebResourceResponse.getStatusCode();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Map<String, String> getResponseHeaders() {
        return mWebResourceResponse.getResponseHeaders();
    }

    @Override
    public String getEncoding() {
        return mWebResourceResponse.getEncoding();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String getReasonPhrase() {
        return mWebResourceResponse.getReasonPhrase();
    }
}
