package com.winjay.practice.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.winjay.practice.utils.LogUtil;

/**
 * 辅助功能
 *
 * @author Winjay
 * @date 2020/4/14
 */
public class AppForegroundAccessibilityService extends AccessibilityService {
    public static final String TAG = AppForegroundAccessibilityService.class.getSimpleName();

    private static String mForegroundPackageName;
    private static volatile AppForegroundAccessibilityService mInstance = null;

    public AppForegroundAccessibilityService() {
    }

    public static AppForegroundAccessibilityService getInstance() {
        if (mInstance == null) {
            synchronized (AppForegroundAccessibilityService.class) {
                if (mInstance == null) {
                    mInstance = new AppForegroundAccessibilityService();
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
}
