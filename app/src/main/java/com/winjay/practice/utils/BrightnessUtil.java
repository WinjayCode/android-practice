package com.winjay.practice.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

/**
 * 屏幕亮度调节工具类
 * <p>
 * 需要系统权限
 * <uses-permission android:name="android.permission.WRITE_SETTINGS"/>
 *
 * @author winjay
 * @date 2019/5/21
 */
public class BrightnessUtil {
    /**
     * 设置屏幕亮度调节模式为手动模式（设置亮度前必须先将自动亮度调节关闭）
     *
     * @param context
     */
    public static void setScrennManualMode(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取系统当前屏幕亮度
     *
     * @param context
     * @return
     */
    public static int getScreenBrightness(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        int defVal = 125;
        return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, defVal);
    }

    /**
     * 设置系统屏幕亮度
     *
     * @param context
     * @param brightness 亮度值(0-255)
     */
    public static void setScreenBrightness(Context context, int brightness) {
        setScrennManualMode(context);
        ContentResolver contentResolver = context.getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    /**
     * 设置窗口亮度
     *
     * @param activity
     * @param brightness 亮度值(0-255)
     */
    public static void setWindowBrightness(Activity activity, int brightness) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        window.setAttributes(lp);
    }
}
