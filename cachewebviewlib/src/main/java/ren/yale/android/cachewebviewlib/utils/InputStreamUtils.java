package ren.yale.android.cachewebviewlib.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ren.yale.android.cachewebviewlib.encode.BytesEncodingDetect;

/**
 * Created by yale on 2017/10/21.
 */

public class InputStreamUtils {

    private InputStream mInputStream;
    private String mEncoding = "UTF-8";
    private int mEncodeBuffer = 500;

    public InputStreamUtils(InputStream inputStream){
        mInputStream = inputStream;
    }

    public void setEncodeBufferSize(int bufferSize){
        if (bufferSize>mEncodeBuffer){
            mEncodeBuffer = bufferSize;
        }
    }
    public String getEncoding(){
        return mEncoding;
    }

    public static String inputStream2Str(InputStream inputStream){
        StringBuffer sb = new StringBuffer();
        if (inputStream == null){
            return sb.toString();
        }
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte buffer[] = new byte[1024];
            int len = 0;
            while ((len = bufferedInputStream.read(buffer,0,1024))>0){
                sb.append(new String(buffer,0,len));
            }
            bufferedInputStream.close();
        }catch (Exception e){
        }
        return sb.toString();

    }

    public InputStream copy(BytesEncodingDetect detect){
        int len;
        byte[] buffer = new byte[mEncodeBuffer];
        if (mInputStream instanceof ByteArrayInputStream){
            try {
                len = mInputStream.read(buffer);
                if (len>0){
                    if (len == mEncodeBuffer){
                        mEncoding = detect.detectEncodingStr(buffer);
                    }else{
                        mEncoding = detect.detectEncodingStr(buffer,len);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mInputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return mInputStream;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean read =false;
        try {
            while ((len = mInputStream.read(buffer)) > -1 ) {
                if (!read){
                    read = true;
                    mEncoding = detect.detectEncodingStr(buffer,len);
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
