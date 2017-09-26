package ren.yale.android.cachewebviewlib;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ren.yale.android.cachewebviewlib.bean.HttpCacheFlag;
import ren.yale.android.cachewebviewlib.utils.AppUtils;
import ren.yale.android.cachewebviewlib.utils.FileUtil;
import ren.yale.android.cachewebviewlib.utils.JsonWrapper;
import ren.yale.android.cachewebviewlib.utils.MD5Utils;
import ren.yale.android.cachewebviewlib.utils.NetworkUtils;

/**
 * Created by yale on 2017/9/22.
 */

public class WebViewCache {

    private DiskLruCache mDiskLruCache;
    private Context mContext;
    private StaticRes mStaticRes;
    private HashMap<String,Map> mHeaderMaps;
    private CacheStrategy mCacheStrategy = CacheStrategy.NORMAL;

    private static class InstanceHolder {
        public static final WebViewCache INSTANCE = new WebViewCache();
    }
    private WebViewCache(){
        mStaticRes = new StaticRes();
    }
    public StaticRes getStaticRes(){
        return mStaticRes;
    }
    public static WebViewCache getInstance(){
        return InstanceHolder.INSTANCE;
    }


    /**
     * Create DiskLruCache
     * @param directory a writable directory
     * @param maxSize the maximum number of bytes this cache should use to store
     * @throws IOException if reading or writing the cache directory fails
     */
    public WebViewCache init(Context context, File directory, long maxSize) throws IOException{
        mHeaderMaps = new HashMap<>();
        mContext = context.getApplicationContext();
        if (mDiskLruCache!=null){
            mDiskLruCache.delete();
            mDiskLruCache = null;
        }
        mDiskLruCache = DiskLruCache.open(directory, AppUtils.getVersionCode(context),1,maxSize);
        return this;
    }
    /**
     * Create DiskLruCache
     * @param directory a writable directory
     * @throws IOException if reading or writing the cache directory fails
     */
    public WebViewCache init(Context context, File directory) throws IOException{
        return  init(context,directory,Integer.MAX_VALUE);
    }
    public void clean(){
        if (mDiskLruCache!=null){
            try {
                FileUtil.deleteDirs(mDiskLruCache.getDirectory().getAbsolutePath(),true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getKey(String url){
        return MD5Utils.getMD5(url,false);
    }
    public static String getFlagKey(String url){
        return getKey(url)+"_";
    }
    private DiskLruCache.Editor getEditor(String key){
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            return editor;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void addHeaderMap(String url, Map<String, String> additionalHttpHeaders){
        if(mHeaderMaps!=null&&additionalHttpHeaders!=null){
            mHeaderMaps.put(url,additionalHttpHeaders);
        }
    }
    public void clearHeaderMap(HashMap<String,Map> map){
        if(mHeaderMaps!=null&&map!=null){
            for (Map.Entry entry : map.entrySet()){
                mHeaderMaps.remove(entry.getKey());
            }
        }
    }

    public void setCacheStrategy(CacheStrategy cacheStrategy){
        mCacheStrategy = cacheStrategy;
    }

    public InputStream httpRequest(String url) {

        try {
            URL urlRequest = new URL(url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) urlRequest.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(30000);

            Map<String,Object> header = mHeaderMaps.get(url);
            if (header!=null){
                for (Map.Entry entry: header.entrySet()){
                    httpURLConnection.setRequestProperty((String)entry.getKey(),(String)entry.getValue());
                }
            }
            HttpCacheFlag local = getCacheFlag(url);
            if (local!=null){
                if (!TextUtils.isEmpty(local.getLastModified())){
                    httpURLConnection.setRequestProperty("If-Modified-Since",local.getLastModified());
                }
                if (!TextUtils.isEmpty(local.getEtag())){
                    httpURLConnection.setRequestProperty("If-None-Match",local.getEtag());
                }
            }
            httpURLConnection.connect();
            HttpCache remote = new HttpCache(httpURLConnection);

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){

                ResourseInputStream resourseInputStream = new ResourseInputStream(httpURLConnection.getInputStream(),
                        getEditor(getKey(url)),getEditor(getFlagKey(url)),remote);
                return resourseInputStream;
            }
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED){
                InputStream  inputStream = getCacheInputStream(url);
                ResourseInputStream resourseInputStream = null;
                if (inputStream == null){
                    resourseInputStream = new ResourseInputStream(httpURLConnection.getInputStream(),
                            getEditor(getKey(url)),getEditor(getFlagKey(url)),remote);
                }else{
                    CacheWebViewLog.d(url+" 304 from cache");
                    resourseInputStream = new ResourseInputStream(inputStream,
                            null,null,remote);
                }
                return resourseInputStream;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private HttpCacheFlag getCacheFlag(String url){
        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot=  mDiskLruCache.get(getFlagKey(url));
            if (snapshot!=null){
                inputStream =  snapshot.getInputStream(0);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                StringBuffer sb = new StringBuffer();
                byte buffer[] = new byte[1024];
                int len = 0;
                while ((len = bufferedInputStream.read(buffer,0,1024))>0){
                    sb.append(new String(buffer,0,len));
                }
                bufferedInputStream.close();
                return new JsonWrapper(sb.toString()).getBean(HttpCacheFlag.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream getCacheInputStream(String url){
        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot=  mDiskLruCache.get(getKey(url));
            if (snapshot!=null){
                inputStream =  snapshot.getInputStream(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    public WebResourceResponse getWebResourceResponse(String url){

        if(mDiskLruCache==null){
            return null;
        }
        if (TextUtils.isEmpty(url)){
            return null;
        }
        if (!url.startsWith("http")){
            return null;
        }
        String extension = MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (TextUtils.isEmpty(extension)){
            return null;
        }
        if (!mStaticRes.canCache(extension)){
            return null;
        }
        InputStream inputStream = null;
        CacheStrategy cacheStrategy = mCacheStrategy;


        if (extension.equals("html")||extension.equals("htm")){
            cacheStrategy = CacheStrategy.NORMAL;
        }

        if (NetworkUtils.isConnected(mContext)){

            if(cacheStrategy == CacheStrategy.NORMAL){
                HttpCacheFlag httpCacheFlag = getCacheFlag(url);
                if (httpCacheFlag!=null&&!httpCacheFlag.isLocalOutDate()){
                    inputStream = getCacheInputStream(url);
                }
            }else if (cacheStrategy == CacheStrategy.FORCE){
                inputStream = getCacheInputStream(url);
            }

        }else{
            inputStream = getCacheInputStream(url);
        }
        if (inputStream==null){
            inputStream = httpRequest(url);
        }else{
            CacheWebViewLog.d(url +": from cache");
        }

        if (inputStream !=null){
            return new WebResourceResponse(mimeType,"utf-8",inputStream);
        }

        return null;

    }

    public enum CacheStrategy{
        NORMAL,FORCE
    }

}
