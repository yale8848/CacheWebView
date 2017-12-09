package ren.yale.android.cachewebviewlib;

import java.io.File;

/**
 * Created by yale on 2017/10/22.
 */

public class CacheStatus {

    private File path = null;
    private boolean exist=false;
    private String extension="";


    @Deprecated
    public File getPath() {
        return path;
    }
    public File getCacheFile(){
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
