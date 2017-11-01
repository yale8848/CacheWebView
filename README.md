# CacheWebView

  [English](https://github.com/yale8848/CacheWebView/blob/master/README_EN.md)

  CacheWebView是Android WebView 缓存的自定义实现，通过拦截静态资源进行内存(LRU)和磁盘(LRU)2级缓存实现缓存。突破系统WebView缓存的空间限制，让缓存更简单、更快、更灵活。

## 使用方式

### 引入库

```
compile 'ren.yale.android:cachewebviewlib:1.1.7'
```

### 修改代码

 - 代码里将WebView改为CacheWebView或者layout xml里修改<WebView 为 <ren.yale.android.cachewebviewlib.CacheWebView

   完毕，其他都不用修改。CacheWebView默认会有内部cache200M 磁盘缓存空间，20M内存缓存空间。同时缓存模式是http默认的缓存模式

---

### 进阶

 - 修改缓存路径和大小,最好在Application中初始化，初始化没有耗时操作
 ```

File cacheFile = new File(this.getCacheDir(),"cache_path_name");
CacheWebView.getWebViewCache().init(this,cacheFile,1024*1024*100,1024*1024*10).enableDebug(true);//100M 磁盘缓存空间,10M 内存缓存空间


 ```

- 预加载，为了访问更快，可以将常用的页面预加载

```
CacheWebView.cacheWebView(this).loadUrl(URL);//要放在UI线程

```

或者

```
 CacheWebView.servicePreload(this,URL);//通过启动Service来预加载，不影响UI线程
```


- 强制缓存，默认是普通缓存，和http缓存模式一样。setCacheStrategy(WebViewCache.CacheStrategy.FORCE),这样对于静态资源直接走缓存，不需要和服务器沟通走304缓存，这样会更快；如果静态资源要更新，
请让web前端同学修改静态资源链接，如给链接加md5值，或者加版本等等方式；

```
CacheWebView webview;
webview.setCacheStrategy(WebViewCache.CacheStrategy.FORCE);

```


- 静态资源后缀映射

  默认磁盘缓存静态资源后缀有：html,htm,js,ico,css,png,jpg,jpeg,gif,bmp,ttf,woff,woff2,otf,eot,svg,xml,swf,txt,text,conf,可以添加删除,addExtension,removeExtension

  默认内存缓存静态资源后缀有：html,htm,js,css,xml,txt,text,conf,可以添加删除,addRamExtension,removeRamExtension

```
CacheWebView.getWebViewCache().getStaticRes().addExtension("swf").removeExtension("svg")
                .addRamExtension("png").removeRamExtension("html");
```

- 设置缓存拦截器，可以针对每一个url是否拦截缓存

```
webview.setCacheInterceptor(new CacheInterceptor() {
            @Override
            public boolean canCache(String url) {
                return true;
            }
 });

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

- 阻塞图片加载，让页面更快加载

  默认没有阻塞图片加载，setBlockNetworkImage(true)后。在页面onPageStarted时阻塞图片加载，onPageFinished时打开图片加载

```
CacheWebView webview;
webview.setBlockNetworkImage(true);
```

- 是否使用自定义缓存，默认是自定义缓存,如果是false，那就和正常的WebView使用一样

```
CacheWebView webview;
webview.setEnableCache(true);
```

- 设置User-Agent

```
CacheWebView webview;
webview.setUserAgent("Android");
```

- 获取缓存文件

```
 CacheStatus cacheStatus = CacheWebView.getWebViewCache().getCacheFile(URL);
 if (cacheStatus.isExist()){
    File file = cacheStatus.getCacheFile();
    String extension = cacheStatus.getExtension();
 }
```

- destroy

```
CacheWebView webview;
webview.destroy();

```

- 页面乱码；默认判断页面编码的buffer大小是500，如果有些中文网站乱码，可以把这个size设置大些

```
CacheWebView.getWebViewCache().setEncodeBufferSize(1024);
```

### 流程图

![CacheWebView流程图](https://camo.githubusercontent.com/12ced3091bbca10ffd7ad0be3d4e3e90370192cc/68747470733a2f2f7374617469632e6f736368696e612e6e65742f75706c6f6164732f696d672f3230313730392f32373135353533375f44446a672e706e673f763d31)


### 博客

[如何让Android WebView访问更快](https://my.oschina.net/yale8848/blog/1544298)