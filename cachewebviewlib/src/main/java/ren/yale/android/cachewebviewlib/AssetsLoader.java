package ren.yale.android.cachewebviewlib;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yale on 2018/7/16.
 */
class AssetsLoader {

    private static volatile  AssetsLoader assetsLoader;
    private Context mContext;
    private HashSet<Pattern> mAssetResSet;
    private String mDir="";

    public static AssetsLoader getInstance() {
        if (assetsLoader==null){
            synchronized (AssetsLoader.class){
                if (assetsLoader==null){
                    assetsLoader = new AssetsLoader();
                }
            }
        }
        return assetsLoader;
    }

    public AssetsLoader init(Context context){
        mContext = context;
        mAssetResSet = new HashSet<>();
        return this;
    }

    private String getUrlPath(String url){
        String uPath="";
        try {
            URL u = new URL(url);
            uPath = u.getPath();
            if (uPath.startsWith("/")){
                if (uPath.length()==1){
                    return uPath;
                }
                uPath = uPath.substring(1);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return uPath;
    }
    public InputStream getResByUrl(String url){


        String uPath = getUrlPath(url);

        if (TextUtils.isEmpty(uPath)){
            return null;
        }

        if (mAssetResSet!=null){
            for (Pattern p: mAssetResSet) {

                Matcher mc = p.matcher(uPath);
                if (mc.find()){
                    String path="";
                    if (TextUtils.isEmpty(mDir)){
                        path = p.pattern();
                    }else{
                        path =  mDir+File.separator+p.pattern();
                    }
                    path = path.substring(0,path.length()-1);
                    return getAssetFileStream(path);
                }
            }
        }
        return null;
    }
    public AssetsLoader setDir(final String dir){
        mDir = dir;
        new Thread(new Runnable() {
            @Override
            public void run() {
                initResource(dir);
            }
        }).start();
        return this;
    }
    private AssetsLoader initResource(String dir){
        try {
            String[] resData = mContext.getAssets().list(dir);
            for (String res : resData) {
                String sub = dir + File.separator + res;
                String[] tmp = mContext.getAssets().list(sub);
                if (tmp.length == 0) {
                    if (!TextUtils.isEmpty(mDir)){
                        sub = sub.replace(mDir + File.separator, "");
                    }
                    mAssetResSet.add( Pattern.compile(sub+"$"));
                } else {
                    initResource(sub);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;

    }
    public InputStream getAssetFileStream(String path) {
        try {
            return mContext.getAssets().open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
