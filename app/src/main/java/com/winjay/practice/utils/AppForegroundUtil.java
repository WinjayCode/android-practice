package com.winjay.practice.utils;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.winjay.practice.accessibility.AccessibilityServiceHelper;

/**
 * 应用前后台判断
 *
 * @author Winjay
 * @date 2020/4/14
 */
public class AppForegroundUtil {
    public static final String TAG = AppForegroundUtil.class.getSimpleName();

    /**
     * 判断应用是否在前台
     *
     * @param context
     * @param packageName 要判断的应用包名
     * @return
     */
    public static boolean isAppRunningForeground(Context context, String packageName) {
        if (AccessibilityServiceHelper.getInstance().isAccessibilitySettingsOn(context)) {
            String foreground = AccessibilityServiceHelper.getInstance().getForegroundPackageName();
            LogUtil.d(TAG, "当前窗口焦点对应包名为：" + foreground);
            return packageName.equals(foreground);
        } else {
            LogUtil.d(TAG, "打开辅助页面");
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return false;
        }
    }
}
