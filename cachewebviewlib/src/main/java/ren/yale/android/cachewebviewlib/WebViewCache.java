package ren.yale.android.cachewebviewlib;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.LruCache;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ren.yale.android.cachewebviewlib.bean.HttpCacheFlag;
import ren.yale.android.cachewebviewlib.bean.RamObject;
import ren.yale.android.cachewebviewlib.disklru.DiskLruCache;
import ren.yale.android.cachewebviewlib.encode.BytesEncodingDetect;
import ren.yale.android.cachewebviewlib.utils.AppUtils;
import ren.yale.android.cachewebviewlib.utils.FileUtil;
import ren.yale.android.cachewebviewlib.utils.InputStreamUtils;
import ren.yale.android.cachewebviewlib.utils.JsonWrapper;
import ren.yale.android.cachewebviewlib.utils.MD5Utils;
import ren.yale.android.cachewebviewlib.utils.NetworkUtils;

/**
 * Created by yale on 2017/9/22.
 */

public class WebViewCache {

    private DiskLruCache mDiskLruCache;
    private StaticRes mStaticRes;
    private Context mContext;
    private String mCacheFilePath;
    private long mCacheSize;
    private long mCacheRamSize;
    private LruCache<String,RamObject> mLruCache;
    private BytesEncodingDetect mEncodingDetect;
    public WebViewCache(){
        mStaticRes = new StaticRes();
        mEncodingDetect = new BytesEncodingDetect();
    }

    public void release(){
        if (mDiskLruCache!=null){
            try {
                mDiskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mStaticRes.clearAll();
        if (mLruCache!=null){
            mLruCache.evictAll();
        }
    }

    public StaticRes getStaticRes(){
        return mStaticRes;
    }

    public WebViewCache openCache(Context context, String directory, long maxSize) throws IOException {

        return openCache(context,directory,maxSize,maxSize/10);
    }
    public WebViewCache openCache(Context context, String directory, long maxDiskSize,long maxRamSize) throws IOException {

        if (mContext==null){
            mContext = context.getApplicationContext();
        }
        CacheConfig cacheConfig = CacheConfig.getInstance();
        mCacheFilePath = cacheConfig.getCacheFilePath()!=null?cacheConfig.getCacheFilePath():directory;
        mCacheSize = cacheConfig.getDiskMaxSize()!=0?cacheConfig.getDiskMaxSize():maxDiskSize;
        mCacheRamSize = cacheConfig.getRamMaxSize()!=0?cacheConfig.getRamMaxSize():maxRamSize;
        if (mDiskLruCache==null){
            mDiskLruCache = DiskLruCache.open(new File(mCacheFilePath), AppUtils.getVersionCode(mContext),3,mCacheSize);
        }
        ensureLruCache();
        return this;
    }
    private void ensureLruCache(){
        if(mLruCache==null){
            synchronized (WebViewCache.class){
                if (mLruCache == null){
                    mLruCache = new LruCache<String,RamObject>((int) mCacheRamSize){
                        @Override
                        protected int sizeOf(String key, RamObject value) {
                            return value.getInputStreamSize()+value.getHttpFlag().getBytes().length;

                        }
                    };
                }
            }
        }
    }

    /**
     * Create DiskLruCache
     * @param directory a writable directory
     * @param maxDiskSize the maximum number of bytes this cache should use to store
     * @throws IOException if reading or writing the cache directory fails
     */
    public WebViewCache init(Context context, String directory, long maxDiskSize){

        mContext = context.getApplicationContext();
        mCacheFilePath = directory;
        mCacheSize = maxDiskSize;
        mCacheRamSize =maxDiskSize/10;
        return this;
    }
    /**
     * Create DiskLruCache
     * @param directory a writable directory
     * @param maxDiskSize the maximum number of bytes this cache should use to store
     * @throws IOException if reading or writing the cache directory fails
     */
    public WebViewCache init(Context context, String directory, long maxDiskSize,long maxRamSize){

        mContext = context.getApplicationContext();
        mCacheFilePath = directory;
        mCacheSize = maxDiskSize;
        mCacheRamSize =maxRamSize;
        return this;
    }
    /**
     * Create DiskLruCache
     * @param directory a writable directory
     * @throws IOException if reading or writing the cache directory fails
     */
    public WebViewCache init(Context context, String directory){
        return  init(context,directory,Integer.MAX_VALUE,20*1024*1024);
    }
    public void clean(){
        if (mDiskLruCache!=null){
            try {
                FileUtil.deleteDirs(mDiskLruCache.getDirectory().getAbsolutePath(),true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mLruCache!=null){
            mLruCache.evictAll();
            mLruCache = null;
        }
    }

    public static String getKey(String url){
        return MD5Utils.getMD5(url,false);
    }
    private DiskLruCache.Editor getEditor(String key){
        try {
            if (mDiskLruCache.isClosed()){
                return null;
            }
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            return editor;
        } catch (IOException e) {
            CacheWebViewLog.d(e.toString());
            e.printStackTrace();
        }
        return null;
    }
    public InputStream httpRequest(CacheWebViewClient client,String url) {

        try {
            URL urlRequest = new URL(url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) urlRequest.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setReadTimeout(30000);

            Map<String,String> header = client.getHeader(url);
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

            httpURLConnection.setRequestProperty("Origin",client.getOriginUrl());
            httpURLConnection.setRequestProperty("Referer",client.getRefererUrl());
            httpURLConnection.setRequestProperty("User-Agent",client.getUserAgent());

            httpURLConnection.connect();
            HttpCache remote = new HttpCache(httpURLConnection);

            int responseCode = httpURLConnection.getResponseCode();
            if ( responseCode== HttpURLConnection.HTTP_OK){

                return new ResourseInputStream(url,httpURLConnection.getInputStream(),
                        getEditor(getKey(url)),remote,mLruCache,mStaticRes);
            }
            if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED){
                InputStream  inputStream = getCacheInputStream(url);
                ResourseInputStream resourseInputStream = null;
                if (inputStream == null){
                    resourseInputStream = new ResourseInputStream(url,httpURLConnection.getInputStream(),
                            getEditor(getKey(url)),remote,mLruCache,mStaticRes);
                }else{
                    CacheWebViewLog.d("304 from cache "+url);
                    return inputStream;
                }
                return resourseInputStream;
            }

        } catch (MalformedURLException e) {
            CacheWebViewLog.d(e.toString()+" "+url);
            e.printStackTrace();
        } catch (IOException e) {
            CacheWebViewLog.d(e.toString()+" "+url);
            e.printStackTrace();
        } catch (Exception e){
            CacheWebViewLog.d(e.toString()+" "+url);
            e.printStackTrace();
        }

        return null;
    }
    private HashMap getAllHttpHeaders(String url){

        HashMap  map = getRamAllHttpHeaders(url);
        if (map!=null){
            return map;
        }
        InputStream inputStream = null;
        try {
            if (mDiskLruCache.isClosed()){
                return null;
            }
            DiskLruCache.Snapshot snapshot=  mDiskLruCache.get(getKey(url));
            if (snapshot!=null){
                inputStream =  snapshot.getInputStream(CacheIndexType.ALL_PROPERTY.ordinal());
                return JsonWrapper.str2Map(InputStreamUtils.inputStream2Str(inputStream));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private HttpCacheFlag getCacheFlag(String url){

        HttpCacheFlag  httpCacheFlag = getRamCacheFlag(url);
        if (httpCacheFlag!=null){
            return httpCacheFlag;
        }
        InputStream inputStream = null;
        try {
            if (mDiskLruCache.isClosed()){
                return null;
            }
            DiskLruCache.Snapshot snapshot=  mDiskLruCache.get(getKey(url));
            if (snapshot!=null){
                inputStream =  snapshot.getInputStream(CacheIndexType.PROPERTY.ordinal());
                return new JsonWrapper(InputStreamUtils.inputStream2Str(inputStream)).getBean(HttpCacheFlag.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private HashMap getRamAllHttpHeaders(String url){

        RamObject ramObject = mLruCache.get(getKey(url));
        if (ramObject!=null){
           return ramObject.getHeaderMap();
        }
        return null;
    }
    private HttpCacheFlag getRamCacheFlag(String url){
        RamObject ramObject = mLruCache.get(getKey(url));
        if (ramObject!=null&&!TextUtils.isEmpty(ramObject.getHttpFlag())){
            try {
                return new JsonWrapper(ramObject.getHttpFlag()).getBean(HttpCacheFlag.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private InputStream getRamCache(String url){
        RamObject ramObject = mLruCache.get(getKey(url));
        if (ramObject!=null){
            InputStream inputStream =  ramObject.getStream();
            if (inputStream!=null){
                try {
                    inputStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return inputStream;
        }
        return null;
    }
    public CacheStatus getCacheFile(String url){
        CacheStatus cacheStatus = new CacheStatus();

        if (TextUtils.isEmpty(url)){
            return cacheStatus;
        }
        File file = mDiskLruCache.getCacheFile(getKey(url),CacheIndexType.CONTENT.ordinal());

        if (file!=null&&file.exists()){
            cacheStatus.setPath(file);
            cacheStatus.setExist(true);
        }
        String extension = MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
        cacheStatus.setExtension(extension);
        return cacheStatus;

    }
    private void disk2ram(String url,DiskLruCache.Snapshot snapshot,InputStream inputStream){
        if (inputStream !=null){
            String extension = MimeTypeMap.getFileExtensionFromUrl(url.toLowerCase());
            if (mStaticRes.canRamCache(extension)){
                InputStream cacheHeader =  snapshot.getInputStream(CacheIndexType.PROPERTY.ordinal());
                InputStream allHeader = snapshot.getInputStream(CacheIndexType.ALL_PROPERTY.ordinal());

                RamObject ramObject = new RamObject();
                String httpFlag = InputStreamUtils.inputStream2Str(cacheHeader);
                String httpAllFlag = InputStreamUtils.inputStream2Str(allHeader);
                ramObject.setHttpFlag(httpFlag);
                ramObject.setStream(inputStream);
                int size= 0;
                try {
                    size = inputStream.available();
                }catch (Exception e){
                }
                ramObject.setInputStreamSize(size);
                mLruCache.put(getKey(url),ramObject);
            }
        }
    }
    private InputStream getCacheInputStream(String url){
        InputStream inputStream = getRamCache(url);
        if (inputStream!=null){
            CacheWebViewLog.d("from ram cache "+url);
            return inputStream;
        }
        try {
            if (mDiskLruCache.isClosed()){
                return null;
            }
            DiskLruCache.Snapshot snapshot=  mDiskLruCache.get(getKey(url));
            if (snapshot!=null){
                inputStream =  snapshot.getInputStream(CacheIndexType.CONTENT.ordinal());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (inputStream!=null){
            CacheWebViewLog.d("from disk cache "+url);
        }
        return inputStream;
    }

    public WebResourceResponse getWebResourceResponse(CacheWebViewClient client,String url,
                                                      CacheStrategy cacheStrategy,
                                                      String encoding,CacheInterceptor cacheInterceptor){

        if(mDiskLruCache==null||mDiskLruCache.isClosed()){
            return null;
        }
        if (TextUtils.isEmpty(url)){
            return null;
        }
        if (!url.startsWith("http")){
            return null;
        }
        CacheWebViewLog.d("visit "+url);

        if (cacheInterceptor!=null){
            if (!cacheInterceptor.canCache(url)){
                return null;
            }
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

        if (mStaticRes.isHtml(extension)){
            cacheStrategy = CacheStrategy.NORMAL;
        }

        ensureLruCache();
        HttpCacheFlag  httpCacheFlag = getCacheFlag(url);
        if (NetworkUtils.isConnected(mContext)){

            if(cacheStrategy == CacheStrategy.NORMAL){
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
            inputStream = httpRequest(client,url);
        }
        String encode = "UTF-8";
        if (!TextUtils.isEmpty(encoding)){
            encode = encoding;
        }
        if (inputStream !=null){
            if (inputStream instanceof ResourseInputStream){

                ResourseInputStream resourseInputStream= (ResourseInputStream) inputStream;

                if (mStaticRes.isCanGetEncoding(extension)&&TextUtils.isEmpty(encoding)){
                    InputStreamUtils inputStreamUtils = new InputStreamUtils(resourseInputStream.getInnerInputStream());
                    inputStreamUtils.setEncodeBufferSize(CacheConfig.getInstance().getEncodeBufferSize());
                    long start = System.currentTimeMillis();
                    InputStream copyInputStream = inputStreamUtils.copy(mEncodingDetect);
                    if (copyInputStream == null){
                        return null;
                    }
                    resourseInputStream.setInnerInputStream(copyInputStream);
                    encode = inputStreamUtils.getEncoding();
                    CacheWebViewLog.d(encode+" "+"get encoding timecost: "+(System.currentTimeMillis()-start)+ "ms "+url);
                }
                resourseInputStream.setEncode(encode);
                WebResourceResponse webResourceResponse=new WebResourceResponse(mimeType,encode
                        ,inputStream);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webResourceResponse.setResponseHeaders(resourseInputStream.getHttpCache().getResponseHeader());
                }

                return webResourceResponse;
            }else{

                Map map = getAllHttpHeaders(url);

                if (httpCacheFlag!=null){
                    encode = httpCacheFlag.getEncode();
                }
                CacheWebViewLog.d(encode +" "+url);
                WebResourceResponse webResourceResponse= new  WebResourceResponse(mimeType,encode,inputStream);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webResourceResponse.setResponseHeaders(map);
                }
                return webResourceResponse;
            }

        }

        return null;

    }

    public enum CacheStrategy{
        NORMAL,FORCE
    }

}
