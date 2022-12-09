package com.winjay.mirrorcast.util;


import android.text.TextUtils;
import android.util.Log;

import com.winjay.mirrorcast.BuildConfig;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Log工具类
 * <p>
 * Process.myPid() 进程号
 * Process.myTid() 当前线程号
 * Process.myUid() 当前调用该进程的用户号
 * Thread.currentThread().getId() 当前线程ID
 * getMainLooper().getThread().getId() 主线程ID
 * getTaskId() 当前Activity所在栈的ID
 * getApplicationInfo().uid 当前调用该应用程序的用户号(和3一样)
 * getApplicationInfo().processName 当前调用该应用程序的进程名
 * *（线程号和线程ID不一样）*
 *
 * @author Winjay
 * @date 2019-10-18
 */
public class LogUtil {
    private static String LOG_PREFIX = "MirrorCast_";
    private static boolean DEBUG = Log.isLoggable(LOG_PREFIX, Log.VERBOSE) || "debug".equalsIgnoreCase(BuildConfig.BUILD_TYPE);

    /**
     * 设置日志模式 true:可打印所有级别日志，false:不会打印v、d级别日志
     */
//    private static boolean DEBUG = false;
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

    public static void v(String tag) {
        v(tag, "");
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

    public static void d(String tag) {
        d(tag, "");
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

    public static void i(String tag) {
        i(tag, "");
    }

    public static void i(String tag, Object arg) {
        String message = arg == null ? "null" : arg.toString();
        Log.i(LOG_PREFIX + tag, buildMessage(message));
    }

    public static void w(Object arg) {
        w("", arg);
    }

    public static void w(String tag) {
        w(tag, "");
    }

    public static void w(String tag, Object arg) {
        String message = arg == null ? "null" : arg.toString();
        Log.w(LOG_PREFIX + tag, buildMessage(message));
    }

    public static void w(String tag, Object arg, Throwable tr) {
        String message = arg == null ? "null" : arg.toString();
        Log.w(LOG_PREFIX + tag, buildMessage(message), tr);
    }

    public static void e(Object arg) {
        e("", arg);
    }

    public static void e(String tag) {
        e(tag, "");
    }

    public static void e(String tag, Object arg) {
        String message = arg == null ? "null" : arg.toString();
        Log.e(LOG_PREFIX + tag, buildMessage(message));
    }

    public static void e(String tag, Object arg, Throwable tr) {
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
        for (StackTraceElement stackTraceElement : trace) {
            if (!stackTraceElement.getClassName().equals(LogUtil.class.getName())) {
//                String callingClass = trace[i].getClassName();
//                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
//                String endWords = callingClass.substring(callingClass.lastIndexOf('$') + 1);
//                if (!isNumeric(endWords)) {
//                    callingClass = endWords;
//                }
//                // 打印类名和方法名
//                caller = callingClass + "." + trace[i].getMethodName();
                // 只打印方法名
                caller = stackTraceElement.getMethodName() + "()";
                break;
            }
        }
        // 打印线程ID、类名、方法名
//        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), caller, args);
        // 打印方法名
        if (TextUtils.isEmpty(args)) {
            return String.format(Locale.US, "%s", caller);
        } else {
            return String.format(Locale.US, "%s: %s", caller, args);
        }
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]+");
        return pattern.matcher(str).matches();
    }
}
