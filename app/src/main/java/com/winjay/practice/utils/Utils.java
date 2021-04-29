package com.winjay.practice.utils;

import android.content.Context;
import android.os.Build;

import java.lang.reflect.Method;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    /**
     * 实现文本复制功能
     *
     * @param text
     */
    public static void copyText(Context context, String text) {
        // 得到剪贴板管理器
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(text.trim());
        } else {
            android.content.ClipboardManager cmb = (android.content.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(text.trim());
        }
    }

    /**
     * 获取Android属性
     *
     * @param key
     * @return
     */
    private String getSystemProperty(String key) {
        String property = "";
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class);
            property = (String) method.invoke(clazz, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "property=" + property);
        return property;
    }
}
