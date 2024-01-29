package com.winjay.mirrorcast.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences工具类
 * <p>
 * SharedPreferences文件的数据会全部保存在内存中，所以不宜存放大数据
 * <p>
 * 1.不支持跨进程，MODE_MULTI_PROCESS 也没用。跨进程频繁读写可能导致数据损坏或丢失。
 * 2.初始化的时候会读取 sp 文件，可能导致后续 getXXX() 方法阻塞。建议提前异步初始化 SharedPreferences。
 * 3.sp 文件的数据会全部保存在内存中，所以不宜存放大数据。
 * 4.edit() 方法每次都会新建一个 EditorImpl 对象。建议一次 edit()，多次 putXXX() 。
 * 5.无论是 commit() 还是 apply() ，针对任何修改都是全量写入。建议针对高频修改的配置项存在子啊单独的 sp 文件。
 * 6.commit() 同步保存，有返回值。apply() 异步保存，无返回值。按需取用。
 * 7.onPause() 、onReceive() 等时机会等待异步写操作执行完成，可能造成卡顿或者 ANR。
 *
 * @author Winjay
 * @date 2019-09-11
 */
public class PreferenceUtil {
    private final static String FILE_NAME = "config";
    private static SharedPreferences sp;
    private static Editor editor;

    /**
     * 初始化的时候会读取sp文件，可能导致后续getXXX()方法阻塞，建议提前异步初始化SharedPreferences
     *
     * @param context
     * @return
     */
    private static SharedPreferences getPreferences(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp;
    }

    /**
     * edit()方法每次都会新建一个EditorImpl对象，建议一次edit()，多次putXXX()
     *
     * @param context
     * @return
     */
    private static Editor getEditor(Context context) {
        if (editor == null) {
            editor = getPreferences(context).edit();
        }
        return editor;
    }

    /**
     * 获得boolean类型的信息,如果没有返回false
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * 获得boolean类型的信息
     *
     * @param context
     * @param key
     * @param defValue ： 没有时的默认值
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    /**
     * 设置boolean类型的 配置数据（同步）
     *
     * @param context
     * @param key
     * @param value
     */
    public static boolean putBooleanSync(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value);
        return getEditor(context).commit();
    }

    /**
     * 设置boolean类型的 配置数据（异步）
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putBooleanAsync(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value);
        getEditor(context).apply();
    }

    /**
     * 获得string类型的信息,如果没有返回null
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    /**
     * 获得String类型的信息
     *
     * @param context
     * @param key
     * @param defValue ： 没有时的默认值
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    /**
     * 设置String类型的 配置数据（同步）
     *
     * @param context
     * @param key
     * @param value
     */
    public static boolean putStringSync(Context context, String key, String value) {
        getEditor(context).putString(key, value);
        return getEditor(context).commit();
    }

    /**
     * 设置String类型的 配置数据（异步）
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putStringAsync(Context context, String key, String value) {
        getEditor(context).putString(key, value);
        getEditor(context).apply();
    }

    /**
     * 获得int类型的信息,如果没有返回-1
     *
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * 获得int类型的信息
     *
     * @param context
     * @param key
     * @param defValue ： 没有时的默认值
     * @return
     */
    public static int getInt(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    /**
     * 设置int类型的 配置数据（同步）
     *
     * @param context
     * @param key
     * @param value
     */
    public static boolean putIntSync(Context context, String key, int value) {
        getEditor(context).putInt(key, value);
        return getEditor(context).commit();
    }

    /**
     * 设置int类型的 配置数据（异步）
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putIntAsync(Context context, String key, int value) {
        getEditor(context).putInt(key, value);
        getEditor(context).apply();
    }

    /**
     * clear all data
     *
     * @param context
     */
    public static void clear(Context context) {
        getEditor(context).clear();
        getEditor(context).commit();
    }
}
