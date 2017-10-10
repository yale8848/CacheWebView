package ren.yale.android.cachewebviewlib.bean;

import java.io.InputStream;

/**
 * Created by yale on 2017/10/10.
 */

public class RamObject {
    private String httpFlag="";
    private InputStream stream;
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
}
