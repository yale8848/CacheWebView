# CacheWebView

[![](https://img.shields.io/badge/jcenter-1.3.8-519dd9.svg)](https://bintray.com/yale8848/maven/CacheWebView/1.3.8)

  CacheWebView is a custom implement of Android WebView, through intercept each request to create ram cache(LRU) and disk cache(LRU). It beyond system WebView cache space
  limit, let cache config more simple ,fast and flexible. Visit website by offline.

## Why use CacheWebView?

- let WebView cache space more bigger, WebView default http protocol space only 18M, and it can not change
- easy config cache,divide static resource and dynamic content,only cache static resource
- if server do not config static file http cache header,CacheWebView also can cache it
- want to get web page resource in cache, e.g , get pic in cache

## Usage, :heart: Very simple :heart:

### use lib

```groovy
compile 'ren.yale.android:cachewebviewlib:1.3.8'
```

### Change code

 - Change `WebView` to `CacheWebView` in code or change `WebView` to `ren.yale.android.cachewebviewlib.CacheWebView` in layout xml


  It is over. CacheWebView default have disk cache space 200M , ram cache space 20M, and cache mode is the same with HTTP protocol cache [HTTP Caching](http://www.cnblogs.com/ppoo24/p/5963037.html).

---

### Further

 - Modify cache path and size, it is usually call in Android Application

 ```Java
File cacheFile = new File(this.getCacheDir(),"cache_path_name");

CacheWebView.getCacheConfig().init(this,cacheFile.getAbsolutePath(),1024*1024*100,1024*1024*10).enableDebug(true);//100M disk space ,10M ram sapce
 ```

- Preload , preload usually request url it will be more fast

```Java
CacheWebView.cacheWebView(this).loadUrl(URL);//this method must call in UI thread
```

or

```Java
 CacheWebView.servicePreload(this,URL);//start a Service to preload
```

- Force cache. Default is Normal cache mode like HTTP cache. Call `setCacheStrategy(WebViewCache.CacheStrategy.FORCE)`, it will force cache every static resource, and it will not connect with server, so it
will not have http 304 status, this mode is very fast. If static resource need to refresh, change static link like add MD5 value or version info etc.


```Java
CacheWebView webview;
webview.setCacheStrategy(WebViewCache.CacheStrategy.FORCE);
```


- Force refresh,if set NO_CACHE mode, CacheWebView cache nothing

```Java
CacheWebView webview;
webview.setCacheStrategy(WebViewCache.CacheStrategy.NO_CACHE);
```


- Static resource suffix cache map

  default disk space static resource suffix:  html,htm,js,ico,css,png,jpg,jpeg,gif,webp,bmp,ttf,woff,woff2,otf,eot,svg,xml,swf,txt,text,conf . Call addExtension and removeExtension to add and remove

  default ram space static resource suffix:  html,htm,js,css,xml,txt,text,conf . Call addRamExtension and removeRamExtension to add and remove

   do not cache: mp4,mp3,ogg,avi,wmv,flv,rmvb,3gp

```Java
//webview instance config
webview.getWebViewCache().getCacheExtensionConfig()
        .addExtension("swf").removeExtension("swf")
        .addRamExtension("svg").removeRamExtension("svg");

//global config
CacheExtensionConfig.addGlobalExtension("swf");
CacheExtensionConfig.removeGlobalExtension("swf");
CacheExtensionConfig.addGlobalRamExtension("svg");
CacheExtensionConfig.removeGlobalRamExtension("svg");
```

- set cache interceptor , whether cache each url

```Java
webview.setCacheInterceptor(new CacheInterceptor() {

            public boolean canCache(String url) {
                return true;
            }
 });
```

- Delete cache

```Java
CacheWebView webview;
webview.clearCache();
```

- Add header

```Java
CacheWebView webview;
webview.loadUrl(URL,getHeaderMap(URL));
```

```Java
@Override
 public boolean shouldOverrideUrlLoading(WebView view, String url) {
     CacheWebView v = (CacheWebView) view;
     v.loadUrl(url,getHeaderMap(url));
     return true;
 }
```

- Block NetworkImage load. Page load more fast

  Default is not block network image. After call `setBlockNetworkImage(true)`, When WebView onPageStarted it will be block, onPageFinished will unblock

```Java
CacheWebView webview;
webview.setBlockNetworkImage(true);
```

- Disable CacheWebView cache function. Default is custom cache mode, call `setEnableCache(false)`, WebView will not these function, it just like normal WebView

```Java
CacheWebView webview;
webview.setEnableCache(true);
```

- Setting User-Agent

```Java
CacheWebView webview;
webview.setUserAgent("Android");
```

- Get cache file

```Java
 CacheStatus cacheStatus = webview.getWebViewCache().getCacheFile(URL);
 if (cacheStatus.isExist()){
    File file = cacheStatus.getCacheFile();
    String extension = cacheStatus.getExtension();
 }
```

- destroy

```Java
CacheWebView webview;
webview.destroy();
```


## How to contribute

   [Contributing Guide](https://github.com/yale8848/CacheWebView/blob/master/CONTRIBUTING.md)
