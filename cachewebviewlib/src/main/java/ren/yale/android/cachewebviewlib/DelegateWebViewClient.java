package ren.yale.android.cachewebviewlib;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DelegateWebViewClient extends WebViewClient {

    private WebViewClient mCustomWebViewClient;

    public void setCustomWebViewClient(WebViewClient webViewClient) {
        mCustomWebViewClient = webViewClient;
    }

    public WebViewClient getCustomWebViewClient() {
        return mCustomWebViewClient;
    }

    @Deprecated
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (mCustomWebViewClient != null && mCustomWebViewClient.shouldOverrideUrlLoading(view, url)) {
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (mCustomWebViewClient != null && shouldOverrideUrlLoading(view, request)) {
            return true;
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onPageStarted(view, url, favicon);
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onPageFinished(view, url);
        }
        super.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onLoadResource(view, url);
        }
        super.onLoadResource(view, url);
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        if (mCustomWebViewClient != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCustomWebViewClient.onPageCommitVisible(view, url);
            }
        }
        super.onPageCommitVisible(view, url);
    }

    @Deprecated
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (mCustomWebViewClient != null) {
            WebResourceResponse resp = mCustomWebViewClient.shouldInterceptRequest(view, url);
            if (resp != null) {
                return resp;
            }
        }
        return super.shouldInterceptRequest(view, url);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (mCustomWebViewClient != null) {
            WebResourceResponse resp = mCustomWebViewClient.shouldInterceptRequest(view, request);
            if (resp != null) {
                return resp;
            }
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Deprecated
    @Override
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
        }
        super.onTooManyRedirects(view, cancelMsg, continueMsg);
    }

    @Deprecated
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
        }
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (mCustomWebViewClient != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCustomWebViewClient.onReceivedError(view, request, error);
            }
        }
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        if (mCustomWebViewClient != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCustomWebViewClient.onReceivedHttpError(view, request, errorResponse);
            }
        }
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onFormResubmission(view, dontResend, resend);
        }
        super.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.doUpdateVisitedHistory(view, url, isReload);
        }
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onReceivedSslError(view, handler, error);
        }
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (mCustomWebViewClient != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCustomWebViewClient.onReceivedClientCertRequest(view, request);
            }
        }
        super.onReceivedClientCertRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (mCustomWebViewClient != null && mCustomWebViewClient.shouldOverrideKeyEvent(view, event)) {
            return true;
        }
        return super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onUnhandledKeyEvent(view, event);
        }
        super.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onScaleChanged(view, oldScale, newScale);
        }
        super.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onReceivedLoginRequest(view, realm, account, args);
        }
        super.onReceivedLoginRequest(view, realm, account, args);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        if (mCustomWebViewClient != null && mCustomWebViewClient.onRenderProcessGone(view, detail)) {
            return true;
        }
        return super.onRenderProcessGone(view, detail);
    }

    @TargetApi(27)
    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
        if (mCustomWebViewClient != null) {
            mCustomWebViewClient.onSafeBrowsingHit(view, request, threatType, callback);
        }
        super.onSafeBrowsingHit(view, request, threatType, callback);
    }
}
