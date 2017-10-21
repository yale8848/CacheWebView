package ren.yale.android.cachewebviewlib.encode;

/**
 * Created by yale on 2017/10/21.
 */

public class Encoding {
    // Supported Encoding Types
    public static int GB2312 = 0;

    public static int GBK = 1;

    public static int GB18030 = 2;

    public static int HZ = 3;

    public static int BIG5 = 4;

    public static int CNS11643 = 5;

    public static int UTF8 = 6;

    public static int UTF8T = 7;

    public static int UTF8S = 8;

    public static int UNICODE = 9;

    public static int UNICODET = 10;

    public static int UNICODES = 11;

    public static int ISO2022CN = 12;

    public static int ISO2022CN_CNS = 13;

    public static int ISO2022CN_GB = 14;

    public static int EUC_KR = 15;

    public static int CP949 = 16;

    public static int ISO2022KR = 17;

    public static int JOHAB = 18;

    public static int SJIS = 19;

    public static int EUC_JP = 20;

    public static int ISO2022JP = 21;

    public static int ASCII = 22;

    public static int OTHER = 23;

    public static int TOTALTYPES = 24;

    public final static int SIMP = 0;

    public final static int TRAD = 1;

    // Names of the encodings as understood by Java
    public static String[] javaname;

    // Names of the encodings for human viewing
    public static String[] nicename;

    // Names of charsets as used in charset parameter of HTML Meta tag
    public static String[] htmlname;

    // Constructor
    public Encoding() {
        javaname = new String[TOTALTYPES];
        nicename = new String[TOTALTYPES];
        htmlname = new String[TOTALTYPES];
        // Assign encoding names
        javaname[GB2312] = "GB2312";
        javaname[GBK] = "GBK";
        javaname[GB18030] = "GB18030";
        javaname[HZ] = "ASCII"; // What to put here? Sun doesn't support HZ
        javaname[ISO2022CN_GB] = "ISO2022CN_GB";
        javaname[BIG5] = "BIG5";
        javaname[CNS11643] = "EUC-TW";
        javaname[ISO2022CN_CNS] = "ISO2022CN_CNS";
        javaname[ISO2022CN] = "ISO2022CN";
        javaname[UTF8] = "UTF-8";
        javaname[UTF8T] = "UTF-8";
        javaname[UTF8S] = "UTF-8";
        javaname[UNICODE] = "Unicode";
        javaname[UNICODET] = "Unicode";
        javaname[UNICODES] = "Unicode";
        javaname[EUC_KR] = "EUC_KR";
        javaname[CP949] = "MS949";
        javaname[ISO2022KR] = "ISO2022KR";
        javaname[JOHAB] = "Johab";
        javaname[SJIS] = "SJIS";
        javaname[EUC_JP] = "EUC_JP";
        javaname[ISO2022JP] = "ISO2022JP";
        javaname[ASCII] = "ASCII";
        javaname[OTHER] = "ISO8859_1";
        // Assign encoding names
        htmlname[GB2312] = "GB2312";
        htmlname[GBK] = "GBK";
        htmlname[GB18030] = "GB18030";
        htmlname[HZ] = "HZ-GB-2312";
        htmlname[ISO2022CN_GB] = "ISO-2022-CN-EXT";
        htmlname[BIG5] = "BIG5";
        htmlname[CNS11643] = "EUC-TW";
        htmlname[ISO2022CN_CNS] = "ISO-2022-CN-EXT";
        htmlname[ISO2022CN] = "ISO-2022-CN";
        htmlname[UTF8] = "UTF-8";
        htmlname[UTF8T] = "UTF-8";
        htmlname[UTF8S] = "UTF-8";
        htmlname[UNICODE] = "UTF-16";
        htmlname[UNICODET] = "UTF-16";
        htmlname[UNICODES] = "UTF-16";
        htmlname[EUC_KR] = "EUC-KR";
        htmlname[CP949] = "x-windows-949";
        htmlname[ISO2022KR] = "ISO-2022-KR";
        htmlname[JOHAB] = "x-Johab";
        htmlname[SJIS] = "Shift_JIS";
        htmlname[EUC_JP] = "EUC-JP";
        htmlname[ISO2022JP] = "ISO-2022-JP";
        htmlname[ASCII] = "ASCII";
        htmlname[OTHER] = "ISO8859-1";
        // Assign Human readable names
        nicename[GB2312] = "GB-2312";
        nicename[GBK] = "GBK";
        nicename[GB18030] = "GB18030";
        nicename[HZ] = "HZ";
        nicename[ISO2022CN_GB] = "ISO2022CN-GB";
        nicename[BIG5] = "Big5";
        nicename[CNS11643] = "CNS11643";
        nicename[ISO2022CN_CNS] = "ISO2022CN-CNS";
        nicename[ISO2022CN] = "ISO2022 CN";
        nicename[UTF8] = "UTF-8";
        nicename[UTF8T] = "UTF-8 (Trad)";
        nicename[UTF8S] = "UTF-8 (Simp)";
        nicename[UNICODE] = "Unicode";
        nicename[UNICODET] = "Unicode (Trad)";
        nicename[UNICODES] = "Unicode (Simp)";
        nicename[EUC_KR] = "EUC-KR";
        nicename[CP949] = "CP949";
        nicename[ISO2022KR] = "ISO 2022 KR";
        nicename[JOHAB] = "Johab";
        nicename[SJIS] = "Shift-JIS";
        nicename[EUC_JP] = "EUC-JP";
        nicename[ISO2022JP] = "ISO 2022 JP";
        nicename[ASCII] = "ASCII";
        nicename[OTHER] = "OTHER";
    }

}
