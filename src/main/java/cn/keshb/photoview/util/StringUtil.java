package cn.keshb.photoview.util;

public class StringUtil {

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return "".equals(str.trim());
    }

}
