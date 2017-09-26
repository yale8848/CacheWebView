package ren.yale.android.cachewebviewlib;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebResourceResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;

import ren.yale.android.cachewebviewlib.utils.CacheWebViewFileUtil;
import ren.yale.android.cachewebviewlib.utils.CacheFastWebViewLog;


/**
 * Created by yale on 2017/9/18.
 */

class ResourceLoader {

    private volatile static ResourceLoader INSTANCE;
    private Context mContext;
    private String mAssetDir;
    private HashSet<String> mAssetResSet;


    public static ResourceLoader getInstance(){
        ResourceLoader tmp = INSTANCE;
        if (tmp ==null){
            synchronized (ResourceLoader.class){
                tmp = INSTANCE;
                if (tmp == null){
                    tmp = new ResourceLoader();
                    INSTANCE = tmp;
                }
            }
        }
        return tmp;
    }

    public  void init(Context context,String assetDir){
        mContext = context.getApplicationContext();
        mAssetDir = assetDir;
        if (mAssetResSet!=null){
            mAssetResSet.clear();
            mAssetResSet = null;
        }
        mAssetResSet = new HashSet<>();
        listRes(mAssetDir);
    }

    private void listRes(String resDir){
        try {
            String[] reses =  mContext.getAssets().list(resDir);
            for (String res : reses){
                String sub = resDir+File.separator+res;
                String[] tmp = mContext.getAssets().list(sub);
                if (tmp.length==0){
                    sub = sub.replace(mAssetDir+File.separator,"");
                    mAssetResSet.add(sub);
                }else{
                    listRes(sub);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getUrlPath(String url){
        try {
            URI u = new URI(url);
            return u.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String findAssetFile(String url){

        int pos = -1;
        while (true){
            if (mAssetResSet.contains(url)){
                break;
            }
            pos = url.indexOf('/');
            if (pos == -1||pos == (url.length()-1)){
                url = "";
                break;
            }
            url = url.substring(pos+1);
        }
        return url;
    }

    public WebResourceResponse getWebResourceResponse(String url){
        String urlPath = getUrlPath(url);
        String mime = CacheWebViewFileUtil.getMIMEType(urlPath);
        if (TextUtils.isEmpty(mime)){
            return null;
        }

        InputStream inputStream = getAssetFileStream(urlPath);
        if (inputStream == null){
            return null;
        }
        return new WebResourceResponse(mime,"utf-8",inputStream);
    }

    public InputStream getAssetFileStream(String urlPath){
        String assetFile = findAssetFile(urlPath);
        if (TextUtils.isEmpty(assetFile)){
            return null;
        }
        try {
            CacheFastWebViewLog.d(urlPath);
            return  mContext.getAssets().open(mAssetDir+File.separator+assetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




}
