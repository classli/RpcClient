package com.sven.common.util;

/**
 * Created by sven on 17/2/13.
 */

public class StringUtil {
    public static boolean isEmpty(String s) {
        if (s == null || s.length()<=0 || s.trim().length() <=0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static String[] split(String str, String separator) {
        if (str != null) {
            return str.split(separator);
        }
        return null;
    }
}
