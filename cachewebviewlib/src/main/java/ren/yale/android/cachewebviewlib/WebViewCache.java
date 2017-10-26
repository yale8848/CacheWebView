package ren.yale.android.cachewebviewlib;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.LruCache;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

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
    private HashMap<String,Map> mHeaderMaps;

    private Context mContext;
    private File mCacheFile;
    private long mCacheSize;
    private long mCacheRamSize;

    private LruCache<String,RamObject> mLruCache;

    private BytesEncodingDetect mEncodingDetect;

    private boolean mDebug = true;
    private int mEncodeBufferSize = 0;

    private static class InstanceHolder {
        public static final WebViewCache INSTANCE = new WebViewCache();
    }
    private WebViewCache(){
        mStaticRes = new StaticRes();
        mEncodingDetect = new BytesEncodingDetect();
    }

    public BytesEncodingDetect getEncodingDetect(){
        return mEncodingDetect;
    }
    public StaticRes getStaticRes(){
        return mStaticRes;
    }
    public static WebViewCache getInstance(){
        return InstanceHolder.INSTANCE;
    }


    public WebViewCache openCache(Context context, File directory, long maxSize) throws IOException {

        return openCache(context,directory,maxSize,maxSize/10);
    }
    public WebViewCache openCache(Context context, File directory, long maxDiskSize,long maxRamSize) throws IOException {

        if (mContext==null){
            mContext = context.getApplicationContext();
        }
        if (mCacheFile==null){
            mCacheFile = directory;
        }
        if (mCacheSize<=0){
            mCacheSize = maxDiskSize;
        }
        if (mCacheRamSize<=0){
            mCacheRamSize = maxRamSize;
        }
        if (mHeaderMaps==null){
            mHeaderMaps = new HashMap<>();
        }
        if (mDiskLruCache==null){
            mDiskLruCache = DiskLruCache.open(mCacheFile, AppUtils.getVersionCode(mContext),3,mCacheSize);
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
                            return value.getInputStreamSize()+value.getHttpFlag().getBytes().length+
                                    value.getAllHttpFlag().getBytes().length;
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
    public WebViewCache init(Context context, File directory, long maxDiskSize){

        mContext = context.getApplicationContext();
        mCacheFile = directory;
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
    public WebViewCache init(Context context, File directory, long maxDiskSize,long maxRamSize){

        mContext = context.getApplicationContext();
        mCacheFile = directory;
        mCacheSize = maxDiskSize;
        mCacheRamSize =maxRamSize;
        return this;
    }
    /**
     * Create DiskLruCache
     * @param directory a writable directory
     * @throws IOException if reading or writing the cache directory fails
     */
    public WebViewCache init(Context context, File directory){
        return  init(context,directory,Integer.MAX_VALUE,20*1024*1024);
    }

    public void setEncodeBuffer(int size){
        mEncodeBufferSize = size;
    }
    public WebViewCache enableDebug(boolean enable){
        mDebug = enable;
        return this;
    }
    public boolean isDebug(){
        return mDebug;
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

    public InputStream httpRequest(WebView view,String url) {

        try {
            URL urlRequest = new URL(url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) urlRequest.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(30000);
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

            CacheWebView cacheWebView = (CacheWebView) view;
            if (cacheWebView!=null){
                httpURLConnection.setRequestProperty("Origin",cacheWebView.getOriginUrl());
                httpURLConnection.setRequestProperty("Referer",cacheWebView.getRefererUrl());
                httpURLConnection.setRequestProperty("User-Agent",cacheWebView.getUserAgent());

            }

            httpURLConnection.connect();
            HttpCache remote = new HttpCache(httpURLConnection);

            int responseCode = httpURLConnection.getResponseCode();
            if ( responseCode== HttpURLConnection.HTTP_OK){

                return new ResourseInputStream(url,httpURLConnection.getInputStream(),
                        getEditor(getKey(url)),remote,mLruCache);
            }
            if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED){
                InputStream  inputStream = getCacheInputStream(url);
                ResourseInputStream resourseInputStream = null;
                if (inputStream == null){
                    resourseInputStream = new ResourseInputStream(url,httpURLConnection.getInputStream(),
                            getEditor(getKey(url)),remote,mLruCache);
                }else{
                    CacheWebViewLog.d("304 from cache "+url);
                    return inputStream;
                    //resourseInputStream = new ResourseInputStream(url,inputStream,
                    //        null,remote,mLruCache);
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
    private Map getAllHttpHeaders(String url){

        Map  map = getRamAllHttpHeaders(url);
        if (map!=null){
            return map;
        }
        InputStream inputStream = null;
        try {
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
    private Map getRamAllHttpHeaders(String url){

        RamObject ramObject = mLruCache.get(getKey(url));
        if (ramObject!=null&&!TextUtils.isEmpty(ramObject.getAllHttpFlag())){
            try {
                return JsonWrapper.str2Map(ramObject.getAllHttpFlag());
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    private InputStream getCacheInputStream(String url){
        InputStream inputStream = getRamCache(url);
        if (inputStream!=null){
            CacheWebViewLog.d("from ram cache "+url);
            return inputStream;
        }
        try {
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

    public WebResourceResponse getWebResourceResponse(WebView view,String url,
                                                      CacheStrategy cacheStrategy,
                                                      String encoding,CacheInterceptor cacheInterceptor){
        if(mDiskLruCache==null){
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
            inputStream = httpRequest(view,url);
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
                    inputStreamUtils.setEncodeBuffer(mEncodeBufferSize);
                    long start = System.currentTimeMillis();
                    InputStream copyInputStream = inputStreamUtils.copy();
                    if (copyInputStream == null){
                        return null;
                    }
                    resourseInputStream.setInnerInputStream(copyInputStream);
                    encode = inputStreamUtils.getEncoding();
                    CacheWebViewLog.d(encode+" "+"get encoding timecost: "+(System.currentTimeMillis()-start)+ " "+url);
                }
                WebResourceResponse webResourceResponse=   new WebResourceResponse(mimeType,encode
                        ,inputStream);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webResourceResponse.setResponseHeaders(resourseInputStream.getHttpCache().getResponseHeader());
                }

                return webResourceResponse;
            }else{
                if (mStaticRes.isCanGetEncoding(extension)&&TextUtils.isEmpty(encoding)){
                    InputStreamUtils inputStreamUtils = new InputStreamUtils(inputStream);
                    inputStreamUtils.setEncodeBuffer(mEncodeBufferSize);
                    long start = System.currentTimeMillis();
                    InputStream copyInputStream = inputStreamUtils.copy();
                    if (copyInputStream == null){
                        return null;
                    }
                    inputStream = copyInputStream;
                    encode = inputStreamUtils.getEncoding();
                    CacheWebViewLog.d(encode+" "+" get encoding timecost: "+(System.currentTimeMillis()-start)+ " "+url);
                }
                Map map = getAllHttpHeaders(url);
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
