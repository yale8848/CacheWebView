package ren.yale.android.cachewebviewlib;

import android.os.Build;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ren.yale.android.cachewebviewlib.utils.MimeTypeMapUtils;

/**
 * Created by yale on 2018/4/23.
 */
public class WebResourceCache {

    private volatile static WebResourceCache INSTANCE=null;
    private WebResourceCache(){}
    public static WebResourceCache getInstance(){
        if (INSTANCE == null){
            synchronized (WebResourceCache.class){
                if (INSTANCE==null){
                    INSTANCE = new WebResourceCache();
                }
            }
        }
        return INSTANCE;
    }

    private Map<String,String> getInnerResponseHeader(HttpURLConnection connection){
        HashMap<String,String> map = new HashMap<>();
        Map<String,List<String>> maps =  connection.getHeaderFields();

        if (maps==null||maps.size()==0){
            return map;
        }

        for (Map.Entry entry: maps.entrySet()){

            if (entry == null){
                continue;
            }
            Object key = entry.getKey();
            if (key == null){
                continue;
            }

            List<String> values = (List<String>) entry.getValue();
            if (values!=null&&values.size()>0){
                map.put((String) entry.getKey(),values.get(0));
            }

        }

        return map;
    }
    private WebResourceResponse getRequestInputStream(WebView view,String url){

        try{

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            connection.setUseCaches(true);

            connection.setRequestProperty("Origin",view.getOriginalUrl());
            connection.setRequestProperty("Referer",view.getOriginalUrl());
            String extension = MimeTypeMapUtils.getFileExtensionFromUrl(url);
            String mimeType = MimeTypeMapUtils.getMimeTypeFromExtension(extension);
            WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType,"",connection.getInputStream());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webResourceResponse.setResponseHeaders(getInnerResponseHeader(connection));
            }
            return webResourceResponse;
        }catch (Exception e){

        }
        return null;
    }

    public WebResourceResponse intercept(WebView view,String url){
        return getRequestInputStream(view,url);
    }




}
