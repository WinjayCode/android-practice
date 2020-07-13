package com.winjay.practice.utils;


import android.text.TextUtils;
import android.util.Log;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Log工具类
 *
 * @author Winjay
 * @date 2019-10-18
 */
public class LogUtil {
    private static String LOG_PREFIX = "Winjay-";
    //    private static final boolean DEBUG = Log.isLoggable(TAG, Log.VERBOSE) || "debug".equalsIgnoreCase(BuildConfig.BUILD_TYPE);
    /**
     * 设置日志模式 true:可打印所有级别日志，false:不会打印v、d级别日志
     */
    private static boolean DEBUG = false;

    public static void setDebugSwitch(boolean debugSwitch) {
        DEBUG = debugSwitch;
    }

    public static void setTagPrefix(String prefix) {
        if (!TextUtils.isEmpty(prefix)) {
            LOG_PREFIX = prefix;
        }
    }

    public static void v(Object arg) {
        v("", arg);
    }

    public static void v(String tag, Object arg) {
        if (DEBUG) {
            String message = arg == null ? "null" : arg.toString();
            Log.v(LOG_PREFIX + tag, buildMessage(message));
        }
    }

    public static void d(Object arg) {
        d("", arg);
    }

    public static void d(String tag, Object arg) {
        if (DEBUG) {
            String message = arg == null ? "null" : arg.toString();
            Log.d(LOG_PREFIX + tag, buildMessage(message));
        }
    }

    public static void i(Object arg) {
        i("", arg);
    }

    public static void i(String tag, Object arg) {
        String message = arg == null ? "null" : arg.toString();
        Log.i(LOG_PREFIX + tag, buildMessage(message));
    }

    public static void w(Object arg) {
        w("", arg);
    }

    public static void w(String tag, Object arg) {
        String message = arg == null ? "null" : arg.toString();
        Log.w(LOG_PREFIX + tag, buildMessage(message));
    }

    public static void e(Object arg) {
        e("", arg);
    }

    public static void e(String tag, Object arg) {
        String message = arg == null ? "null" : arg.toString();
        Log.e(LOG_PREFIX + tag, buildMessage(message));
    }

    public static void e(Throwable tr, String tag, Object arg) {
        String message = arg == null ? "null" : arg.toString();
        Log.e(LOG_PREFIX + tag, buildMessage(message), tr);
    }

    public static void wtf(String tag, Object arg) {
        String message = arg == null ? "null" : arg.toString();
        Log.wtf(LOG_PREFIX + tag, buildMessage(message));
    }

    public static void wtf(Throwable tr, String tag, Object arg) {
        String message = arg == null ? "null" : arg.toString();
        Log.wtf(LOG_PREFIX + tag, buildMessage(message), tr);
    }

    private static String buildMessage(String args) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                String endWords = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                if (!isNumeric(endWords)) {
                    callingClass = endWords;
                }
                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), caller, args);
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]+");
        return pattern.matcher(str).matches();
    }
}
