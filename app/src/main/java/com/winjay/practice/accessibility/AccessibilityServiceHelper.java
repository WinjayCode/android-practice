package com.winjay.practice.accessibility;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.winjay.practice.utils.LogUtil;

import java.util.List;

/**
 * 无障碍服务帮助类
 *
 * @author Winjay
 * @date 2022-12-16
 */
public class AccessibilityServiceHelper {
    private static final String TAG = AccessibilityServiceHelper.class.getSimpleName();

    /**
     * 辅助功能是否打开
     *
     * @param context
     * @param className
     * @return
     */
    public static boolean isServiceON(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(100);
        if (runningServices.size() < 0) {
            return false;
        }

        for (int i = 0; i < runningServices.size(); i++) {
            ComponentName service = runningServices.get(i).service;
            if (service.getClassName().contains(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 打开无障碍服务
     *
     * @param context
     */
    public static void requirePermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开辅助功能
     * <p>
     * AndroidManifest.xml中需要配置这两个权限（系统权限）
     * <uses-permission android:name="android.permission.WRITE_SETTINGS" />
     * <uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS" />
     *
     * @param context
     */
    public static void openAccessibilitySetting(Context context, String className) {
        Settings.Secure.putString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                context.getPackageName() + "/" + className);
        Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 1);
    }

    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            LogUtil.d(TAG, "error=" + e.getMessage());
        }
        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }
        return false;
    }
}
