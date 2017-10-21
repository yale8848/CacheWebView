package ren.yale.android.cachewebviewlib.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ren.yale.android.cachewebviewlib.WebViewCache;

/**
 * Created by yale on 2017/10/21.
 */

public class InputStreamCopy {

    private InputStream mInputStream;
    private String mEncoding = "UTF-8";

    public InputStreamCopy(InputStream inputStream){
        mInputStream = inputStream;
    }

    public String getEncoding(){
        return mEncoding;
    }

    public InputStream copy(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        boolean read =false;
        try {
            while ((len = mInputStream.read(buffer)) > -1 ) {
                if (!read){
                    read = true;
                    mEncoding = WebViewCache.getInstance().getEncodingDetect().detectEncodingStr(buffer);
                }
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }





}
