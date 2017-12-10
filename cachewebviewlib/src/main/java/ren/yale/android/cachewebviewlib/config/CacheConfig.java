package ren.yale.android.cachewebviewlib.config;

import android.content.Context;

/**
 * Created by yale on 2017/11/1.
 */

public class CacheConfig {

    private String cacheFilePath;
    private long diskMaxSize = 200*1024*1024;
    private long ramMaxSize = diskMaxSize/10;
    private int encodeBufferSize = 500;
    private boolean debug = true;

    private Context mContext;

    private static CacheConfig mCacheConfig;

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public static CacheConfig getInstance(){

        if (mCacheConfig == null){
            synchronized (CacheConfig.class){
                if (mCacheConfig == null){
                    mCacheConfig = new CacheConfig();
                }
            }
        }
        return mCacheConfig;
    }
    public CacheConfig init(Context context, String directory, long maxDiskSize, long maxRamSize){

        mContext = context.getApplicationContext();
        cacheFilePath = directory;
        diskMaxSize = maxDiskSize;
        ramMaxSize =maxRamSize;
        return this;
    }

    public String getCacheFilePath() {
        return cacheFilePath;
    }

    public void setCacheFilePath(String cacheFilePath) {
        this.cacheFilePath = cacheFilePath;
    }

    public long getDiskMaxSize() {
        return diskMaxSize;
    }

    public void setDiskMaxSize(long diskMaxSize) {
        this.diskMaxSize = diskMaxSize;
    }

    public long getRamMaxSize() {
        return ramMaxSize;
    }

    public void setRamMaxSize(long ramMaxSize) {
        this.ramMaxSize = ramMaxSize;
    }

    public int getEncodeBufferSize() {
        return encodeBufferSize;
    }

    public CacheConfig setEncodeBufferSize(int encodeBufferSize) {
        this.encodeBufferSize = encodeBufferSize;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public CacheConfig enableDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
}
