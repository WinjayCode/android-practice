package com.winjay.practice.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;

import com.winjay.practice.utils.LogUtil;

/**
 * 辅助功能
 *
 * @author Winjay
 * @date 2020/4/14
 */
public class AccessibilityServiceHelper extends AccessibilityService {
    public static final String TAG = AccessibilityServiceHelper.class.getSimpleName();

    private static String mForegroundPackageName;
    private static volatile AccessibilityServiceHelper mInstance = null;

    public AccessibilityServiceHelper() {
    }

    public static AccessibilityServiceHelper getInstance() {
        if (mInstance == null) {
            synchronized (AccessibilityServiceHelper.class) {
                if (mInstance == null) {
                    mInstance = new AccessibilityServiceHelper();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            mForegroundPackageName = event.getPackageName().toString();
            LogUtil.d(TAG, "mForegroundPackageName=" + mForegroundPackageName);
        }
    }

    @Override
    public void onInterrupt() {
    }

    public String getForegroundPackageName() {
        return mForegroundPackageName;
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
    public void openAccessibilitySetting(Context context) {
        Settings.Secure.putString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                context.getPackageName() + "/" + AccessibilityServiceHelper.class.getCanonicalName());
        Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 1);
    }

    public boolean isAccessibilitySettingsOn(Context context) {
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
