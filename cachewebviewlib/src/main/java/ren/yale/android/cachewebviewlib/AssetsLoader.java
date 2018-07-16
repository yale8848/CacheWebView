package ren.yale.android.cachewebviewlib;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    public InputStream getResByUrl(String url){
        if (mAssetResSet!=null){
            for (Pattern p: mAssetResSet) {
                Matcher mc = p.matcher(url);
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
    public AssetsLoader setDir(String dir){
        mDir = dir;
        initResource(dir);
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
