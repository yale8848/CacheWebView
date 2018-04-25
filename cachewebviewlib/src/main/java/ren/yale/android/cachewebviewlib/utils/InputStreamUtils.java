package ren.yale.android.cachewebviewlib.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;


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

}
