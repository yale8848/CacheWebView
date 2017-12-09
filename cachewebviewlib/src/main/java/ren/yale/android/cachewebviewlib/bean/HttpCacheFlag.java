package ren.yale.android.cachewebviewlib.bean;

import android.text.TextUtils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ren.yale.android.cachewebviewlib.utils.TimeUtils;


/**
 * Created by yale on 2017/9/24.
 */

public class HttpCacheFlag {
    private String cacheControl="";
    private String etag="";
    private String expires="";
    private String lastModified="";
    private String pragma="";
    private String currentTime;

    private String encode="UTF-8";

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getPragma() {
        return pragma;
    }

    public void setPragma(String pragma) {
        this.pragma = pragma;
    }

    private boolean isLocalOutDateByCacheControl(){
        if (!TextUtils.isEmpty(getCacheControl())){
            Pattern pattern = Pattern.compile("max-age=(\\d+)");
            Matcher matcher = pattern.matcher(getCacheControl());
            if (matcher.find()){
                String time = matcher.group(1);
                try {
                    Integer nt = Integer.valueOf(time);
                    if (nt == 0){
                        return true;
                    }
                    Date last = TimeUtils.getStardTime(getCurrentTime());
                    return !TimeUtils.compare(new Date(last.getTime()+nt*1000),new Date());

                }catch (Exception e){
                }
            }

        }
        return true;
    }

    public boolean isLocalOutDate(){

        if (!TextUtils.isEmpty(getExpires())){
            Date d = TimeUtils.formatGMT(getExpires());
            if (d == null){
                return isLocalOutDateByCacheControl();
            }
            boolean ret = TimeUtils.compare(new Date(),d);
            if (ret){
                return isLocalOutDateByCacheControl();
            }
            return ret;
        }else{
            return isLocalOutDateByCacheControl();
        }

    }
    public boolean isRemoteOutDate(HttpCacheFlag httpCacheFlag){

        if (!TextUtils.isEmpty(getEtag())&&!TextUtils.isEmpty(httpCacheFlag.getEtag())){
            return !getEtag().equals(httpCacheFlag.getEtag());
        }else if (!TextUtils.isEmpty(getLastModified())&&!TextUtils.isEmpty(httpCacheFlag.getLastModified())){
            return !getLastModified().equals(httpCacheFlag.getLastModified());
        }
        return true;
    }
}
