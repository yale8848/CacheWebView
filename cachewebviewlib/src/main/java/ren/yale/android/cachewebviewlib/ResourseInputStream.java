package ren.yale.android.cachewebviewlib;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yale on 2017/9/22.
 */

class ResourseInputStream extends InputStream {

    private OutputStream mOutputStream;
    private OutputStream mOutputStreamProperty;
    private InputStream mInnerInputStream;
    private int mCurrenReadLength;
    private DiskLruCache.Editor mEditorContent;
    private DiskLruCache.Editor mEditorProperty;
    private HttpCache mHttpCache;
    private String mUrl="";

    public ResourseInputStream(String url,InputStream inputStream,
                               DiskLruCache.Editor content,DiskLruCache.Editor property,HttpCache httpCache){


        mUrl = url;
        mInnerInputStream = inputStream;
        mHttpCache = httpCache;
        mEditorProperty = property;
        mEditorContent = content;
        getStream(content,property);
    }

    private void getStream(DiskLruCache.Editor content,DiskLruCache.Editor property){
        if (content == null||property == null){
            return;
        }
        try {
            mOutputStream = content.newOutputStream(0);
            mOutputStreamProperty = property.newOutputStream(0);
        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public void close() throws IOException {
        mInnerInputStream.close();

        if (mOutputStream!=null&&mOutputStreamProperty!=null){
            mOutputStream.flush();
            String flag = mHttpCache.getCacheFlagString();
            mOutputStreamProperty.write(flag.getBytes());
            mOutputStreamProperty.flush();
            mEditorContent.commit();
            mEditorProperty.commit();
            mOutputStreamProperty.close();
            mOutputStream.close();

            CacheWebViewLog.d(mUrl +" cached");
        }else if (mEditorProperty!=null&&mEditorContent!=null){
            mEditorContent.abort();
            mEditorProperty.abort();
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
        void finish();
    }
}
