package ren.yale.android.cachewebviewlib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yale on 2017/9/25.
 */

public class TimeUtils {
    private static final String STARD_FROMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date formatGMT(String time) {
        if (time.indexOf("GMT") < 0) {
            try {
                long tt = Long.valueOf(time);
                return new Date(tt * 1000);
            } catch (Exception e) {
            }
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        try {
            Date date = sdf.parse(time.trim());
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean compare(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        return d1.getTime() - d2.getTime() > 0;
    }

    public static Date getStardTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(STARD_FROMAT);
        try {
            Date date = sdf.parse(time);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getStardTime(Long time) {
        try {
            Date date = new Date(time * 1000);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(STARD_FROMAT);
        return sdf.format(new Date());
    }
}
