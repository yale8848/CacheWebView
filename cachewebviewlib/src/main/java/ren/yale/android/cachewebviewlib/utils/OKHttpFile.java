package ren.yale.android.cachewebviewlib.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;

import okio.ByteString;
import okio.GzipSource;
import okio.Okio;
import okio.Source;

/**
 * Created by yale on 2018/7/16.
 */
public class OKHttpFile {

    private static final int ENTRY_METADATA = 0;
    private static final int ENTRY_BODY = 1;

    public static InputStream getCacheFile(File path, String url){

        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String key = ByteString.encodeUtf8(url.toString()).md5().hex();
        File entryFile =  new File(path.getAbsolutePath(),  key+"."+ENTRY_METADATA);
        File bodyFile =  new File(path.getAbsolutePath(),  key+"."+ENTRY_BODY);
        if (entryFile != null && entryFile.exists()&&bodyFile!=null&&bodyFile.exists()) {
            try {
                BufferedReader fr = new BufferedReader(new FileReader(entryFile),1024);
                String line="";
                boolean isGzip = false;
                while ((line = fr.readLine())!=null){
                    if (line.contains("Content-Encoding")&&
                            line.contains("gzip")){
                        isGzip = true;
                        break;
                    }
                }
                InputStream inputStream = new FileInputStream(bodyFile);
                if (!isGzip){
                    return inputStream;
                }else{
                    Source source = Okio.source(bodyFile);
                    source = new GzipSource(source);
                   return Okio.buffer(source).inputStream();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}
