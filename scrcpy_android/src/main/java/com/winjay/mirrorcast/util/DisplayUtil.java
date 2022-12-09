package com.winjay.mirrorcast.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * 显示相关工具类
 *
 * @author Winjay
 * @date 2020/8/12
 */
public class DisplayUtil {
    private static final String TAG = DisplayUtil.class.getSimpleName();

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    protected int sp2px(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static DisplayMetrics getScreenMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 获取屏幕分辨率
     *
     * @return
     */
    public static int[] getScreenSize(Context context) {
        int[] size = new int[3];
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) (context.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getRealMetrics(outMetrics);
        size[0] = outMetrics.widthPixels;
        LogUtil.d(TAG, "width=" + size[0]);
        size[1] = outMetrics.heightPixels;
        LogUtil.d(TAG, "height=" + size[1]);
        size[2] = outMetrics.densityDpi;
        LogUtil.d(TAG, "densityDpi=" + size[2]);
        return size;
    }

    /**
     * 状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            statusBarHeight = 0;
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
