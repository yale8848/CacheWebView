package ren.yale.android.cachewebviewlib.bean;

import java.io.InputStream;

/**
 * Created by yale on 2017/10/10.
 */

public class RamObject {
    private String httpFlag="";
    private String allHttpFlag = "";


    private InputStream stream;
    private int inputStreamSize = 0;

    public int getInputStreamSize() {
        return inputStreamSize;
    }

    public void setInputStreamSize(int inputStreamSize) {
        this.inputStreamSize = inputStreamSize;
    }

    public InputStream getStream() {
        return stream;
    }
    public void setStream(InputStream stream) {
        this.stream = stream;
    }
    public String getHttpFlag() {
        return httpFlag;
    }

    public void setHttpFlag(String httpFlag) {
        this.httpFlag = httpFlag;
    }
    public String getAllHttpFlag() {
        return allHttpFlag;
    }

    public void setAllHttpFlag(String allHttpFlag) {
        this.allHttpFlag = allHttpFlag;
    }
}
