# CacheWebView

  CacheWebView is a custom implement of Android WebView, through intercept each request to create ram cache(LRU) and disk cache(LRU). It beyond system WebView cache space
  limit, let cache config more simple ,fast and flexible.

## Usage

### use lib

```
compile 'ren.yale.android:cachewebviewlib:1.1.7'
```

### Change code

 - Change WebView to CacheWebView in code or change <WebView to <ren.yale.android.cachewebviewlib.CacheWebView in layout xml

  It is over. CacheWebView default have disk cache space 200M , ram cache space 20M, and cache mode is the same with HTTP protocol cache [HTTP Caching](http://www.cnblogs.com/ppoo24/p/5963037.html).

---

### Further

 - Modify cache path and size, it is usually call in Android Application

 ```

File cacheFile = new File(this.getCacheDir(),"cache_path_name");
CacheWebView.getWebViewCache().init(this,cacheFile,1024*1024*100,1024*1024*10).enableDebug(true);//100M disk space ,10M ram sapce


 ```

- Preload , preload usually request url it will be more fast

```
CacheWebView.cacheWebView(this).loadUrl(URL);//this method must call in UI thread

```

or

```
 CacheWebView.servicePreload(this,URL);//start a Service to preload
```

- Force cache. Default is Normal cache mode like HTTP cache. Call `setCacheStrategy(WebViewCache.CacheStrategy.FORCE)`, it will force cache every static resource, and it will not connect with server, so it
will not have http 304 status, this mode is very fast. If static resource need to refresh, change static link like add MD5 value or version info etc.


```
CacheWebView webview;
webview.setCacheStrategy(WebViewCache.CacheStrategy.FORCE);

```

- Static resource suffix map

  default disk space static resource suffix:  html,htm,js,ico,css,png,jpg,jpeg,gif,bmp,ttf,woff,woff2,otf,eot,svg,xml,swf,txt,text,conf . Call addExtension and removeExtension to add and remove

  default ram space static resource suffix:  html,htm,js,css,xml,txt,text,conf . Call addRamExtension and removeRamExtension to add and remove


```
CacheWebView.getWebViewCache().getStaticRes().addExtension("swf").removeExtension("svg")
                .addRamExtension("png").removeRamExtension("html");
```


- set cache interceptor , whether cache each url

```
webview.setCacheInterceptor(new CacheInterceptor() {
            @Override
            public boolean canCache(String url) {
                return true;
            }
 });

```

- Delete cache

```
CacheWebView webview;
webview.clearCache();

```

or

```
CacheWebView.getWebViewCache().clean();
```

- Add header

```
CacheWebView webview;
webview.loadUrl(URL,getHeaderMap(URL));
```

```
@Override
 public boolean shouldOverrideUrlLoading(WebView view, String url) {
     CacheWebView v = (CacheWebView) view;
     v.loadUrl(url,getHeaderMap(url));
     return true;
 }
```

- Block NetworkImage load. Page load more fast

  Default is not block network image. After call `setBlockNetworkImage(true)`, When WebView onPageStarted it will be block, onPageFinished will unblock

```
CacheWebView webview;
webview.setBlockNetworkImage(true);
```

- Disable CacheWebView cache function. Default is custom cache mode, call `setEnableCache(false)`, WebView will not these function, it just like normal WebView

```
CacheWebView webview;
webview.setEnableCache(true);
```

- Setting User-Agent

```
CacheWebView webview;
webview.setUserAgent("Android");
```

- Get cache file

```
 CacheStatus cacheStatus = CacheWebView.getWebViewCache().getCacheFile(URL);
 if (cacheStatus.isExist()){
    File file = cacheStatus.getPath();
    String extension = cacheStatus.getExtension();
 }
```

- destroy

```
CacheWebView webview;
webview.destroy();

```