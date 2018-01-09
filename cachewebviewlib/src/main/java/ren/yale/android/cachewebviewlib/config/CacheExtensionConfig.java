package ren.yale.android.cachewebviewlib.config;

import android.text.TextUtils;

import java.util.HashSet;

/**
 * Created by yale on 2017/9/26.
 */

public class CacheExtensionConfig
{
    //全局默认的
    private static HashSet STATIC = new HashSet()
    {
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
            add("webp");
        }
    };
    private static HashSet NO_CACH = new HashSet()
    {
        {
            add("mp4");
            add("mp3");
            add("ogg");
            add("avi");
            add("wmv");
            add("flv");
            add("rmvb");
            add("3gp");
        }
    };
    private static HashSet STATIC_RAM = new HashSet()
    {
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
    //单独webview实例的
    private HashSet statics = new HashSet(STATIC);
    private HashSet no_cache = new HashSet(NO_CACH);
    private HashSet statics_ram = new HashSet(STATIC_RAM);

    public static void addGlobalExtension(String extension)
    {
        add(STATIC, extension);
    }

    public static void removeGlobalExtension(String extension)
    {
        remove(STATIC, extension);
    }

    public static void addGlobalRamExtension(String extension)
    {
        add(STATIC_RAM, extension);
    }

    public static void removeGlobalRamExtension(String extension)
    {
        remove(STATIC_RAM, extension);
    }

    private static void add(HashSet set, String extension)
    {
        if (TextUtils.isEmpty(extension))
        {
            return;
        }
        set.add(extension.replace(".", "").toLowerCase().trim());
    }

    private static void remove(HashSet set, String extension)
    {
        if (TextUtils.isEmpty(extension))
        {
            return;
        }
        set.remove(extension.replace(".", "").toLowerCase().trim());
    }

    public boolean isMedia(String extension)
    {
        if (TextUtils.isEmpty(extension))
        {
            return false;
        }
        if (NO_CACH.contains(extension)){
            return true;
        }
        return no_cache.contains(extension.toLowerCase().trim());
    }

    public boolean canCache(String extension)
    {

        if (TextUtils.isEmpty(extension))
        {
            return false;
        }
        extension = extension.toLowerCase().trim();
        if (STATIC.contains(extension)){
            return true;
        }
        return statics.contains(extension);

    }

    public boolean canRamCache(String extension)
    {

        if (TextUtils.isEmpty(extension))
        {
            return false;
        }
        extension = extension.toLowerCase().trim();
        if (STATIC_RAM.contains(extension)){
            return true;
        }
        return statics_ram.contains(extension);

    }

    public CacheExtensionConfig addExtension(String extension)
    {
        add(statics, extension);
        return this;
    }

    public CacheExtensionConfig removeExtension(String extension)
    {
        remove(statics, extension);
        return this;
    }

    public CacheExtensionConfig addRamExtension(String extension)
    {
        add(statics_ram, extension);
        return this;
    }

    public CacheExtensionConfig removeRamExtension(String extension)
    {
        remove(statics_ram, extension);
        return this;
    }


    public boolean isCanGetEncoding(String extension)
    {
        if (TextUtils.isEmpty(extension))
        {
            return false;
        }
        if (isHtml(extension))
        {
            return true;
        }
        if (extension.toLowerCase().equals("js") ||
                extension.toLowerCase().equals("css"))
        {
            return true;
        }
        return false;
    }

    public boolean isHtml(String extension)
    {
        if (TextUtils.isEmpty(extension))
        {
            return false;
        }
        if (extension.toLowerCase().equals("html") ||
                extension.toLowerCase().equals("htm"))
        {
            return true;
        }
        return false;
    }

    public void clearAll()
    {
        clearRamExtension();
        clearDiskExtension();
    }

    public void clearRamExtension()
    {

        statics_ram.clear();
    }

    public void clearDiskExtension()
    {
        statics.clear();
    }

}
