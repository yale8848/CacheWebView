package ren.yale.android.cachewebviewlib;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by yale on 2018/7/16.
 */
class AssetsLoader {

    private static volatile  AssetsLoader assetsLoader;
    private Context mContext;
    private CopyOnWriteArraySet<String> mAssetResSet;
    private String mDir="";
    private boolean mCleared = false;
    private boolean mIsSuffixMod=false;

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

    public AssetsLoader isAssetsSuffixMod(boolean suffixMod){
        mIsSuffixMod = suffixMod;
        return this;
    }
    public AssetsLoader init(Context context){
        mContext = context;
        mAssetResSet = new CopyOnWriteArraySet<>();
        mCleared = false;
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
        if (!mIsSuffixMod){
            if (TextUtils.isEmpty(mDir)){
                return getAssetFileStream(uPath);
            }else{
                return getAssetFileStream(mDir+File.separator+uPath);
            }
        }
        if (mAssetResSet!=null){
            for (String p: mAssetResSet) {
                if (uPath.endsWith(p)){
                    if (TextUtils.isEmpty(mDir)){
                        return getAssetFileStream(p);
                    }else{
                        return getAssetFileStream(mDir+File.separator+p);
                    }
                }
            }
        }
        return null;
    }
    public AssetsLoader setDir(final String dir){
        mDir = dir;
        return this;
    }
    public AssetsLoader initData(){

        if (!mIsSuffixMod){
            return this;
        }
        if (mAssetResSet.size()==0){
           new Thread(new Runnable() {
               @Override
               public void run() {
                   initResourceNoneRecursion(mDir);
               }
           }).start();
        }
        return this;
    }

    public void clear(){
        mCleared = true;
        if (mAssetResSet!=null&&mAssetResSet.size()>0){
            mAssetResSet.clear();
        }
    }

    private void addAssetsFile(String file){

        String flag = mDir+File.separator;
        if (!TextUtils.isEmpty(mDir)){
            int pos = file.indexOf(flag);
            if (pos>=0){
                file = file.substring(pos+flag.length());
            }
        }
        mAssetResSet.add(file);
    }
    private AssetsLoader initResourceNoneRecursion(String dir){

        try {

            LinkedList<String> list = new LinkedList<String>();
            String[] resData = mContext.getAssets().list(dir);
            for (String res : resData) {
                String sub = dir + File.separator + res;
                String[] tmp = mContext.getAssets().list(sub);
                if (tmp.length == 0) {
                    addAssetsFile(sub);
                } else {
                    list.add(sub);
                }
            }
            while (!list.isEmpty()){
                if (mCleared){
                    break;
                }
                String last = list.removeFirst();
                String[] tmp = mContext.getAssets().list(last);
                if (tmp.length == 0) {
                    addAssetsFile(last);
                } else {
                    for(String sub : tmp){
                        String[] tmp1 = mContext.getAssets().list(last+File.separator+sub);
                        if (tmp1.length == 0){
                            addAssetsFile(last+File.separator+sub);
                        }else{
                            list.add(last+File.separator+sub);
                        }
                    }
                }
            }
        } catch (IOException e) {
           // e.printStackTrace();
        }
        return this;

    }
    public InputStream getAssetFileStream(String path) {
        try {
            return mContext.getAssets().open(path);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return null;
    }

}
