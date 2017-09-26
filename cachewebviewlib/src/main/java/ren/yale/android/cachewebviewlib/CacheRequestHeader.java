package ren.yale.android.cachewebviewlib;

import java.util.Map;

/**
 * Created by yale on 2017/9/26.
 */

public interface CacheRequestHeader {
    Map<String,String> getRequestHeader(String url);
}
