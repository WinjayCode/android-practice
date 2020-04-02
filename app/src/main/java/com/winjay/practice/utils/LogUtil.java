package com.winjay.practice.utils;


import android.util.Log;

/**
 * Log工具类
 *
 * @author Winjay
 * @date 2019-10-18
 */
public class LogUtil {

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }
}
