package ren.yale.android.cachewebviewlib.utils;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

/**
 * Created by yale on 2018/1/9.
 */

public class MimeTypeMapUtils {

    public static String getFileExtensionFromUrl(String url) {
        url = url.toLowerCase();
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }

            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!filename.isEmpty()) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }

        return "";
    }
    public static String getMimeTypeFromUrl(String url) {
        return  MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtensionFromUrl(url));
    }
    public static String getMimeTypeFromExtension(String extension) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
