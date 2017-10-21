package ren.yale.android.cachewebviewlib;

import android.text.TextUtils;

import java.util.HashSet;

/**
 * Created by yale on 2017/9/26.
 */

public class StaticRes {
    private static HashSet STATIC = new HashSet(){
        {
            add("html");
            add("htm");
            add("js");
            add("ico");
            add("css");
            add("png");
            add("jpg");
            add("jpeg");
            add("gif");
            add("bmp");
            add("ttf");
            add("woff");
            add("woff2");
            add("otf");
            add("eot");
            add("svg");
            add("xml");
            add("swf");
            add("txt");
            add("text");
            add("conf");
        }
    };

    private static HashSet STATIC_RAM = new HashSet(){
        {
            add("html");
            add("htm");
            add("js");
            add("css");
            add("xml");
            add("txt");
            add("text");
            add("conf");
        }
    };

    public boolean canCache(String extension){

        if (TextUtils.isEmpty(extension)){
            return false;
        }
        return STATIC.contains(extension.toLowerCase().trim());

    }
    public boolean canRamCache(String extension){

        if (TextUtils.isEmpty(extension)){
            return false;
        }
        return STATIC_RAM.contains(extension.toLowerCase().trim());

    }
    private void add(HashSet set,String extension){
        if (TextUtils.isEmpty(extension)){
            return ;
        }
        extension = extension.replaceAll(".","");
        set.add(extension.toLowerCase().trim());
    }
    private void remove(HashSet set,String extension){
        if (TextUtils.isEmpty(extension)){
            return;
        }
        extension = extension.replaceAll(".","");
        set.remove(extension.toLowerCase().trim());
    }
    public StaticRes addExtension(String extension){
        add(STATIC,extension);
        return this;
    }
    public StaticRes removeExtension(String extension){
        remove(STATIC,extension);
        return this;
    }
    public StaticRes addRamExtension(String extension){
        add(STATIC_RAM,extension);
        return this;
    }

    public StaticRes removeRamExtension(String extension){
        remove(STATIC_RAM,extension);
        return this;
    }
    public  boolean isCanGetEncoding(String extension){
        if (TextUtils.isEmpty(extension)){
            return false;
        }
        if (isHtml(extension)){
            return true;
        }
        if (extension.toLowerCase().equals("js")||
                extension.toLowerCase().equals("css")){
            return true;
        }
        return false;
    }
    public  boolean isHtml(String extension){
        if (TextUtils.isEmpty(extension)){
            return false;
        }
        if (extension.toLowerCase().equals("html")||
                extension.toLowerCase().equals("htm")){
            return true;
        }
        return false;
    }

}
