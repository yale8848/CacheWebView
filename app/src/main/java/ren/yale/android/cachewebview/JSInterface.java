package ren.yale.android.cachewebview;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by yale on 2018/7/11.
 */
public class JSInterface {

    private Context mContext;

    public JSInterface(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void toast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}
