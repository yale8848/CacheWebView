package ren.yale.android.cachewebviewlib;

import android.text.TextUtils;

import java.net.HttpURLConnection;

import ren.yale.android.cachewebviewlib.bean.HttpCacheFlag;
import ren.yale.android.cachewebviewlib.utils.JsonWrapper;
import ren.yale.android.cachewebviewlib.utils.TimeUtils;


/**
 * Created by yale on 2017/9/24.
 */

class HttpCache {


    private HttpCacheFlag mHttpCacheFlag;

    public HttpCache(HttpURLConnection connection){
        mHttpCacheFlag = new HttpCacheFlag();

        mHttpCacheFlag.setCacheControl(connection.getHeaderField(CacheFlag.Cache_Control.value()));
        mHttpCacheFlag.setEtag(connection.getHeaderField(CacheFlag.ETag.value()));
        mHttpCacheFlag.setExpires(connection.getHeaderField(CacheFlag.Expires.value()));
        mHttpCacheFlag.setLastModified(connection.getHeaderField(CacheFlag.Last_Modified.value()));
        mHttpCacheFlag.setPragma(connection.getHeaderField(CacheFlag.Pragma.value()));
        mHttpCacheFlag.setCurrentTime(TimeUtils.getCurrentTime());
    }
    public HttpCacheFlag getHttpCacheFlag(){
        return mHttpCacheFlag;
    }
    public boolean isCanCache(){

        if (!TextUtils.isEmpty(mHttpCacheFlag.getPragma())){
            return mHttpCacheFlag.getPragma().equals(CacheConrolType.no_cache.value());
        }else if (!TextUtils.isEmpty(mHttpCacheFlag.getCacheControl())){
            return mHttpCacheFlag.getCacheControl().equals(CacheConrolType.no_cache.value())||
                    mHttpCacheFlag.getCacheControl().equals(CacheConrolType.no_store.value());
        }
        return false;
    }
    public boolean isNoCache(){

        if (!TextUtils.isEmpty(mHttpCacheFlag.getPragma())){
            return mHttpCacheFlag.getPragma().equals(CacheConrolType.no_cache.value());
        }else if (!TextUtils.isEmpty(mHttpCacheFlag.getCacheControl())){
            return mHttpCacheFlag.getCacheControl().equals(CacheConrolType.no_cache.value())||
                    mHttpCacheFlag.getCacheControl().equals(CacheConrolType.no_store.value());
        }
        return false;
    }

    public String getCacheFlagString(){
        JsonWrapper jsonWrapper = new JsonWrapper();

        try {
            return jsonWrapper.obj2JosnStr(mHttpCacheFlag,HttpCacheFlag.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private enum CacheFlag{
        Cache_Control("Cache-Control"),
        Last_Modified("Last-Modified"),
        ETag("ETag"),
        Expires("Expires"),
        Pragma("Pragma");

        private String mFlag;
        CacheFlag(String flag) {
            mFlag = flag;
        }
        public String value(){
            return mFlag;
        }
    }
    private enum CacheConrolType{
        Public("Public"),
        Private("Private"),
        max_age("max-age"),
        no_cache("no-cache"),
        no_store("no-store");

        private String mValue;
        CacheConrolType(String value) {
            mValue = value;
        }
        public String value(){
            return mValue;
        }
    }
}
