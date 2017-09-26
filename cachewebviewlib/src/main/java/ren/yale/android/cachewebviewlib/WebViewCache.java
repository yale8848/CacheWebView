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
import java.util.Map;

import ren.yale.android.cachewebviewlib.bean.HttpCacheFlag;
import ren.yale.android.cachewebviewlib.utils.AppUtils;
import ren.yale.android.cachewebviewlib.utils.CacheFastWebViewLog;
import ren.yale.android.cachewebviewlib.utils.FileUtil;
import ren.yale.android.cachewebviewlib.utils.JsonWrapper;
import ren.yale.android.cachewebviewlib.utils.MD5Utils;
import ren.yale.android.cachewebviewlib.utils.NetworkUtils;

/**
 * Created by yale on 2017/9/22.
 */

public class WebViewCache {

    private DiskLruCache mDiskLruCache;
    private CacheRequestHeader mCacheRequestHeader;
    private Context mContext;
    private StaticRes mStaticRes;

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
    public void init(Context context, File directory, long maxSize) throws IOException{
        mContext = context.getApplicationContext();
        mDiskLruCache = DiskLruCache.open(directory, AppUtils.getVersionCode(context),1,maxSize);
    }
    public void init(Context context, File directory) throws IOException{
        init(context,directory,Integer.MAX_VALUE);
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
        return MD5Utils.getMD5(url,false)+"_";
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
    public void setCacheRequestHeader(CacheRequestHeader cacheRequestHeader) {
        mCacheRequestHeader = cacheRequestHeader;
    }
    public InputStream httpRequest(String url) {

        try {
            URL urlRequest = new URL(url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) urlRequest.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(30000);

            if (mCacheRequestHeader!=null){
                Map<String,String> header = mCacheRequestHeader.getRequestHeader(url);
                if (header!=null){
                    for (Map.Entry entry: header.entrySet()){
                        httpURLConnection.setRequestProperty((String)entry.getKey(),(String)entry.getValue());
                    }
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

                ResourseInputStream resourseInputStream = new ResourseInputStream(url,httpURLConnection.getInputStream(),
                        httpURLConnection.getContentLength(),getEditor(getKey(url)),getEditor(getFlagKey(url)),remote);
                return resourseInputStream;
            }
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED){
                InputStream  inputStream = getCacheInputStream(url);
                ResourseInputStream resourseInputStream = null;
                if (inputStream == null){
                    resourseInputStream = new ResourseInputStream(url,httpURLConnection.getInputStream(),
                            httpURLConnection.getContentLength(),getEditor(getKey(url)),getEditor(getFlagKey(url)),remote);
                }else{
                    CacheFastWebViewLog.d(url+" 304 from cache");
                    resourseInputStream = new ResourseInputStream(url,inputStream,
                            httpURLConnection.getContentLength(),null,null,remote);
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
        String extension = MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (TextUtils.isEmpty(extension)){
            return null;
        }
        if (!mStaticRes.canCache(extension)){
            return null;
        }
        InputStream inputStream = null;

        if (NetworkUtils.isConnected(mContext)){
            HttpCacheFlag httpCacheFlag = getCacheFlag(url);
            if (httpCacheFlag!=null&&!httpCacheFlag.isLocalOutDate()){
                inputStream = getCacheInputStream(url);
            }
        }else{
            inputStream = getCacheInputStream(url);
        }
        if (inputStream==null){
            inputStream = httpRequest(url);
        }else{
            CacheFastWebViewLog.d(url +": from cache");
        }

        if (inputStream !=null){
            return new WebResourceResponse(mimeType,"utf-8",inputStream);
        }

        return null;

    }

}
