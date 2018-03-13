# CacheWebView

[![](https://img.shields.io/badge/jcenter-1.3.8-519dd9.svg)](https://bintray.com/yale8848/maven/CacheWebView/1.3.8)

  [English](https://github.com/yale8848/CacheWebView/blob/master/README_EN.md)

  CacheWebView是Android WebView 缓存的自定义实现，通过拦截静态资源进行内存(LRU)和磁盘(LRU)2级缓存实现缓存。突破系统WebView缓存的空间限制，让缓存更简单、更快、更灵活。让网站离线也能正常访问。

## 为什么要用CacheWebView

- 让WebView缓存空间更大，WebView默认的http协议缓存空间只有十几兆，并且不能修改
- 缓存可以灵活配置，实现动态内容和静态内容分离，缓存静态内容
- 对于服务端没有对静态资源的header配置http缓存字段时，可以强制缓存，这样会更快
- 想方便的拿到web缓存资源，比如说从缓存中拿页面已经加载过的图片

## 使用方式，:heart: 很简单 :heart:

### 引入库

```groovy
compile 'ren.yale.android:cachewebviewlib:1.3.8'
```

### 修改代码

 - 代码里将`WebView`改为`CacheWebView`或者layout xml里修改`WebView`为`ren.yale.android.cachewebviewlib.CacheWebView`


   完毕，因为CacheWebView继承WebView，其他都不用修改。CacheWebView默认会有内部cache200M 磁盘缓存空间，20M内存缓存空间。同时缓存模式是http默认的缓存模式。

---

### 进阶

 - 修改缓存路径和大小,最好在Application中初始化，初始化没有耗时操作

 ```Java
File cacheFile = new File(this.getCacheDir(),"cache_path_name");

CacheWebView.getCacheConfig().init(this,cacheFile.getAbsolutePath(),1024*1024*100,1024*1024*10)
.enableDebug(true);//100M 磁盘缓存空间,10M 内存缓存空间
 ```

- 预加载，为了访问更快，可以将常用的页面预加载

```Java
CacheWebView.cacheWebView(this).loadUrl(URL);//要放在UI线程
```

或者

```Java
 CacheWebView.servicePreload(this,URL);//通过启动Service来预加载，不影响UI线程
```


- 强制缓存，默认是普通缓存，和http缓存模式一样。setCacheStrategy(WebViewCache.CacheStrategy.FORCE),这样对于静态资源直接走缓存，不需要和服务器沟通走304缓存，这样会更快；如果静态资源要更新，
请让web前端同学修改静态资源链接，如给链接加md5值，或者加版本等等方式；

```Java
CacheWebView webview;
webview.setCacheStrategy(WebViewCache.CacheStrategy.FORCE);
```

- 强制刷新，如果设置强制刷新，CacheWebView不会缓存任何数据

```Java
CacheWebView webview;
webview.setCacheStrategy(WebViewCache.CacheStrategy.NO_CACHE);
```


- 静态资源后缀缓存映射

  默认磁盘缓存静态资源后缀有：html,htm,js,ico,css,png,jpg,jpeg,gif,webp,bmp,ttf,woff,woff2,otf,eot,svg,xml,swf,txt,text,conf

  不会缓存的有：mp4,mp3,ogg,avi,wmv,flv,rmvb,3gp

  可以添加删除,addExtension,removeExtension

  默认内存缓存静态资源后缀有：html,htm,js,css,xml,txt,text,conf

  可以添加删除,addRamExtension,removeRamExtension

```Java

//webview实例添加删除
webview.getWebViewCache().getCacheExtensionConfig()
        .addExtension("swf").removeExtension("swf")
        .addRamExtension("svg").removeRamExtension("svg");

//全局添加删除
CacheExtensionConfig.addGlobalExtension("swf");
CacheExtensionConfig.removeGlobalExtension("swf");
CacheExtensionConfig.addGlobalRamExtension("svg");
CacheExtensionConfig.removeGlobalRamExtension("svg");

```
- 设置缓存拦截器，可以针对每一个url是否拦截缓存

```Java
webview.setCacheInterceptor(new CacheInterceptor() {
            public boolean canCache(String url) {
                return true;
            }
 });
```

- 删除缓存

```Java
CacheWebView webview;
webview.clearCache();
```


- 添加header

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

- 阻塞图片加载，让页面更快加载

  默认没有阻塞图片加载，setBlockNetworkImage(true)后。在页面onPageStarted时阻塞图片加载，onPageFinished时打开图片加载

```Java
CacheWebView webview;
webview.setBlockNetworkImage(true);
```

- 是否使用自定义缓存，默认是自定义缓存,如果是false，那就和正常的WebView使用一样

```Java
CacheWebView webview;
webview.setEnableCache(true);
```

- 设置User-Agent

```Java
CacheWebView webview;
webview.setUserAgent("Android");
```

- 获取缓存文件

```Java
 CacheStatus cacheStatus =  webview.getWebViewCache().getCacheFile(URL);
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

- 页面乱码：

  如果你清楚网站的编码，最好调用类似webview.setEncoding("UTF-8")主动设置，这样效率高，如果不知道那就忽略，程序自动判断，默认判断页面编码的buffer大小是500，如果有些中文网站乱码，可以把这个size设置大些

```Java
CacheWebView.getCacheConfig().setEncodeBufferSize(1024);
```

### 流程图

![CacheWebView流程图](https://camo.githubusercontent.com/12ced3091bbca10ffd7ad0be3d4e3e90370192cc/68747470733a2f2f7374617469632e6f736368696e612e6e65742f75706c6f6164732f696d672f3230313730392f32373135353533375f44446a672e706e673f763d31)


## 贡献

   [如何贡献代码](https://github.com/yale8848/CacheWebView/blob/master/CONTRIBUTING.md)

### 博客

[如何让Android WebView访问更快](https://my.oschina.net/yale8848/blog/1544298)
