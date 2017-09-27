# CacheWebView

  CacheWebView是Android WebView 缓存的自定义实现，通过拦截静态资源进行缓存。突破系统WebView缓存的空间限制，让缓存更简单、灵活。

## 使用方式

### 引入库

```
compile 'ren.yale.android:cachewebviewlib:0.4'
```

### 修改代码

 - 将 WebView 改为ren.yale.android.cachewebviewlib.CacheWebView

  完毕，其他都不用修改。CacheWebView默认会有内部cache200M的空间，同时缓存模式是http默认的模式

---

### 进阶

 - 修改缓存路径和大小,最好在Application中初始化，初始化没有耗时操作
 ```

File cacheFile = new File(this.getCacheDir(),"cache_path_name");
CacheWebView.getWebViewCache().init(this,cacheFile,1024*1024*100);//100M


 ```

- 预加载，为了访问更快，可以将常用的页面预加载，要放在UI线程

```
 CacheWebView.preLoad(this,URL);

```

- 强制缓存，默认是普通缓存，setCacheStrategy(WebViewCache.CacheStrategy.FORCE),这样对于静态资源直接走缓存，不需要和服务器沟通走304缓存，这样会更快；如果静态资源要更新，
请让web前端同学修改静态资源链接，如给链接加md5值，或者加版本等等方式；

```
File cacheFile = new File(this.getCacheDir(),"cache_path_name");
CacheWebView.getWebViewCache().init(this,cacheFile,1024*1024*100).
setCacheStrategy(WebViewCache.CacheStrategy.FORCE);

```

或者

```
CacheWebView.getWebViewCache().setCacheStrategy(WebViewCache.CacheStrategy.FORCE);
```

- 静态资源后缀映射

  默认静态资源后缀有：html,htm,js,css,png,jpg,jpeg,gif,bmp,ttf,woff,woff2,otf,eot,svg,xml,swf,txt,text,conf,可以添加删除

```
CacheWebView.getWebViewCache().getStaticRes().addExtension("aaa").removeExtension("bbb");
```

- 删除缓存

```
CacheWebView webview;
webview.clearCache();

```

或者

```
CacheWebView.getWebViewCache().clean();
```

- 添加header

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


### 流程图

![CacheWebView流程图](https://static.oschina.net/uploads/img/201709/27155537_DDjg.png?v=1)

