package com.winjay.practice.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import com.winjay.practice.utils.LogUtil;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class FloatView {
    private static final String TAG = FloatView.class.getSimpleName();
    private static FloatView instance;
    private Context mContext;
    /**
     * 窗口管理器
     */
    private WindowManager mWindowManager;
    /**
     * 窗口属性
     */
    private WindowManager.LayoutParams mLayoutParams;
    /**
     * WindowManager是否addview
     */
    private boolean hasAdded;

    public static FloatView getInstance() {
        if (instance == null) {
            synchronized (FloatView.class) {
                if (instance == null) {
                    instance = new FloatView();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.mContext = context;
        doInit();
    }

    private void doInit() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        initLayoutParams();
    }

    /**
     * 初始化WindowManager属性
     */
    private void initLayoutParams() {
        mLayoutParams = new WindowManager.LayoutParams();
        //设置window type
        if (Build.VERSION.SDK_INT > 24) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            LogUtil.d(TAG, "WindowManager.LayoutParams.TYPE_PHONE;");
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            LogUtil.d(TAG, "WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;");
        }

        //设置图片格式，效果为背景透明
        mLayoutParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        // 设置横竖屏属性
        mLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    /**
     * windowmanange添加Kui卡片
     */
    public void addView(View view) {
        if (!hasAdded) {
            LogUtil.d(TAG, "WindowManager addView");
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            mWindowManager.addView(view, mLayoutParams);
            hasAdded = true;
        }
    }

    /**
     * windowmananger移除Kui卡片
     */
    public void removeView(View view) {
        if (hasAdded) {
            LogUtil.d(TAG, "WindowManager removeView");
            mWindowManager.removeView(view);
            hasAdded = false;
        }
    }
}
