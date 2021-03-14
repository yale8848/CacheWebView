package ren.yale.android.cachewebviewlib;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Description: 加载动态设置的缓存文件
 * @Author: po1arbear
 * @Date: 2021/3/11 7:14 PM
 *
 */
public class DynamicCacheLoader {

  private static volatile DynamicCacheLoader INSTANCE;


  public static DynamicCacheLoader getInstance() {
    if (INSTANCE == null) {
      synchronized (DynamicCacheLoader.class) {
        if (INSTANCE == null) {
          INSTANCE = new DynamicCacheLoader();
        }
      }
    }
    return INSTANCE;
  }


  public File getResByUrl(File file, String url) {
    String uPath = getUrlPath(url);
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      for (File child : files) {
        if (child.isDirectory()) {
          File targetFile = getResByUrl(child, url);
          if (targetFile != null) {
            return targetFile;
          }
        } else {
          String fileName = child.getName();
          if (uPath.endsWith(fileName)) {
            return child;
          }
        }
      }
    } else {
      String fileName = file.getName();
      if (uPath.endsWith(fileName)) {
        return file;
      }
    }
    return null;
  }


  public String getUrlPath(String url) {
    String uPath = "";
    try {
      URL u = new URL(url);
      uPath = u.getPath();
      if (uPath.startsWith("/")) {
        if (uPath.length() == 1) {
          return uPath;
        }
        uPath = uPath.substring(1);
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    return uPath;
  }


}
