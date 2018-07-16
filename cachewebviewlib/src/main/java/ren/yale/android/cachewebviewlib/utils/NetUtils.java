package ren.yale.android.cachewebviewlib.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yale on 2017/9/15.
 */

public class NetUtils {

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static  String getOriginUrl(String referer) {
        String ou = referer;
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

    public static Map<String,String> multimapToSingle(Map<String, List<String>> maps){

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
}
