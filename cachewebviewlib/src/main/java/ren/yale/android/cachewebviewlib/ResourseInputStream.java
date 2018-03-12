package ren.yale.android.cachewebviewlib;

import android.util.LruCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ren.yale.android.cachewebviewlib.bean.RamObject;
import ren.yale.android.cachewebviewlib.config.CacheExtensionConfig;
import ren.yale.android.cachewebviewlib.disklru.DiskLruCache;
import ren.yale.android.cachewebviewlib.utils.JsonWrapper;
import ren.yale.android.cachewebviewlib.utils.MimeTypeMapUtils;

/**
 * Created by yale on 2017/9/22.
 */

class ResourseInputStream extends InputStream {

    private OutputStream mOutputStream;
    private OutputStream mOutputStreamProperty;
    private OutputStream mOutputStreamAllProperty;
    private InputStream mInnerInputStream;
    private int mCurrenReadLength;
    private DiskLruCache.Editor mEditorContent;
    private HttpCache mHttpCache;
    private String mUrl="";
    private LruCache mLruCache;
    private ByteArrayOutputStream mRamArray;
    private CacheExtensionConfig mCacheExtensionConfig;



    private String mEncode;

    private int mFileSize;



    public ResourseInputStream(String url,InputStream inputStream,
                               DiskLruCache.Editor content,HttpCache httpCache,LruCache lrucache,
                               CacheExtensionConfig cacheExtensionConfig,int fileSize){
        mUrl = url;
        mInnerInputStream = inputStream;
        mHttpCache = httpCache;
        mEditorContent = content;
        mLruCache = lrucache;
        mCacheExtensionConfig = cacheExtensionConfig;
        getStream(content);
        mFileSize = fileSize;
    }

    public String getEncode() {
        return mEncode;
    }

    public void setEncode(String encode) {
        mEncode = encode;
    }

    public void setInnerInputStream(InputStream innerInputStream){
        mInnerInputStream = innerInputStream;
    }

    public InputStream getInnerInputStream(){
        return mInnerInputStream;
    }

    public HttpCache getHttpCache(){
        return mHttpCache;
    }

    private void getStream(DiskLruCache.Editor content){
        if (content == null){
            return;
        }
        try {
            mOutputStream = content.newOutputStream(CacheIndexType.CONTENT.ordinal());
            mOutputStreamProperty = content.newOutputStream(CacheIndexType.PROPERTY.ordinal());
            mOutputStreamAllProperty = content.newOutputStream(CacheIndexType.ALL_PROPERTY.ordinal());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String extension = MimeTypeMapUtils.getFileExtensionFromUrl(mUrl);
        if (mCacheExtensionConfig.canRamCache(extension)){
            mRamArray = new ByteArrayOutputStream();
        }
    }

    @Override
    public int read( byte[] b) throws IOException {
        int count =  mInnerInputStream.read(b);
        writeStream(b,0,count);
        return count;
    }
    private void writeStream( byte[] b,  int off, int len){

        if (mOutputStream==null){
            return;
        }
        if (len>0){
            mCurrenReadLength+=len;
            try {
                mOutputStream.write(b,off,len);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mRamArray!=null){
                mRamArray.write(b,off,len);
            }
        }


    }
    @Override
    public int read( byte[] b, int off, int len) throws IOException {
        int count = mInnerInputStream.read(b, off, len);
        writeStream(b,off,count);
        return count;
    }
    @Override
    public long skip(long n) throws IOException {
        return mInnerInputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return mInnerInputStream.available();
    }

    private void streamClose() throws Exception{
        mInnerInputStream.close();

        if (mOutputStream!=null&&mOutputStreamProperty!=null){

            if (mFileSize>0&&mCurrenReadLength!=mFileSize){

                if (mEditorContent!=null){
                    mEditorContent.abort();
                }
                return;
            }

            mHttpCache.setEncode(mEncode);
            String flag = mHttpCache.getCacheFlagString();
            String allFlag = JsonWrapper.map2Str(mHttpCache.getResponseHeader());
            if (mRamArray!=null){
                try {
                    RamObject ram = new RamObject();
                    byte[] buffer = mRamArray.toByteArray();
                    ram.setStream(new ByteArrayInputStream(buffer));
                    ram.setHttpFlag(flag);
                    ram.setHeaderMap(mHttpCache.getResponseHeader());
                    ram.setInputStreamSize(buffer.length+allFlag.getBytes().length);
                    mLruCache.put(WebViewCache.getKey(mUrl),ram);
                    CacheWebViewLog.d("ram cached "+mUrl);
                }catch (Exception e){
                }
            }

            mOutputStream.flush();
            mOutputStreamAllProperty.write(allFlag.getBytes());
            mOutputStreamAllProperty.flush();
            mOutputStreamProperty.write(flag.getBytes());
            mOutputStreamProperty.flush();
            mEditorContent.commit();

            mOutputStreamProperty.close();
            mOutputStream.close();
            mOutputStreamAllProperty.close();
            CacheWebViewLog.d("disk cached "+mUrl);
        }else if (mEditorContent!=null){
            mEditorContent.abort();
        }
    }
    @Override
    public void close() throws IOException {

        try {
            streamClose();
        }catch (Exception e){

        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        mInnerInputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        mInnerInputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return mInnerInputStream.markSupported();
    }

    @Override
    public int read() throws IOException {
        return mInnerInputStream.read();
    }

    public interface IWriteFinish{
        void close(String content,String flag );
    }
}
