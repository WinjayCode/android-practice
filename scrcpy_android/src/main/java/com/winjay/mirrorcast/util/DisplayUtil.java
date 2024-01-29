package com.winjay.mirrorcast.util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.winjay.mirrorcast.AppApplication;

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

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * @param orientation 虚拟屏想要的方向
     * @return 虚拟屏ID
     */
    public static int createVirtualDisplay(int orientation) {
        int displayId = -1;
        int width = 0;
        int height = 0;
        try {
            LogUtil.d(TAG);
            DisplayManager displayManager = (DisplayManager) AppApplication.context.getSystemService(Context.DISPLAY_SERVICE);
            int[] screenSize = getScreenSize(AppApplication.context);
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                if (isPortrait(AppApplication.context)) {
                    width = screenSize[1];
                    height = screenSize[0];
                } else {
                    width = screenSize[0];
                    height = screenSize[1];
                }
            }
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                if (isPortrait(AppApplication.context)) {
                    width = screenSize[0];
                    height = screenSize[1];
                } else {
                    width = screenSize[1];
                    height = screenSize[0];
                }
            }
            LogUtil.d(TAG, "width=" + width + ", height=" + height);
            int flags = 139;
//            int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION |
//                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC |
//                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;
            VirtualDisplay display = displayManager.createVirtualDisplay("app_mirror",
                    width, height, screenSize[2], new SurfaceView(AppApplication.context).getHolder().getSurface(),
                    flags);
            displayId = display.getDisplay().getDisplayId();
            LogUtil.d(TAG, "virtual display ID=" + displayId);

//            PackageManager packageManager = AppApplication.context.getPackageManager();
//            boolean ret = packageManager.hasSystemFeature(PackageManager.FEATURE_ACTIVITIES_ON_SECONDARY_DISPLAYS);
//            LogUtil.d(TAG, "onCreate: have " + PackageManager.FEATURE_ACTIVITIES_ON_SECONDARY_DISPLAYS + "   " + ret);
//
//            Intent intent = new Intent();
//            intent.setComponent(new ComponentName("com.winjay.mirrorcast","com.winjay.mirrorcast.car.server.CarLauncherActivity"));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//
//            ActivityOptions options = ActivityOptions.makeBasic();
//            options.setLaunchDisplayId(displayId);
//            Bundle optsBundle = options.toBundle();
//
//            AppApplication.context.startActivity(intent, optsBundle);

//            send(Constants.APP_REPLY_VIRTUAL_DISPLAY_ID + Constants.COMMAND_SPLIT + displayId);
        } catch (Exception e) {
            LogUtil.e(TAG, "createVirtualDisplay error " + e.getMessage());
            e.printStackTrace();
        }
        return displayId;
    }

    /*private void createVirtualDisplay() {
        try {
            LogUtil.d(TAG);
            DisplayManager displayManager = (DisplayManager) AppApplication.context.getSystemService(Context.DISPLAY_SERVICE);
            int[] screenSize = DisplayUtil.getScreenSize(AppApplication.context);

            int flags = 139;

//            int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION |
//                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC |
//                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;

            VirtualDisplay virtualDisplay = displayManager.createVirtualDisplay("app_mirror",
                    screenSize[0], screenSize[1], screenSize[2], new SurfaceView(AppApplication.context).getHolder().getSurface(),
                    flags);
            int displayId = virtualDisplay.getDisplay().getDisplayId();
            LogUtil.d(TAG, "virtual display ID=" + displayId);

            for (Display display : displayManager.getDisplays()) {
                LogUtil.d(TAG, "dispaly: " + display.getName() + ", id " + display.getDisplayId() + " :" + display.toString());
//                if (display.getDisplayId() != 0) {
//                    SecondeDid = display.getDisplayId();
//                }
            }

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.winjay.mirrorcast", "com.winjay.mirrorcast.car.server.CarLauncherActivity"));

            ActivityOptions activityOptions = ActivityOptions.makeBasic();
            MediaRouter mediaRouter = (MediaRouter) AppApplication.context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
            MediaRouter.RouteInfo route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
            if (route != null) {
                Display presentationDisplay = route.getPresentationDisplay();
                LogUtil.d(TAG, "displayId=" + presentationDisplay.getDisplayId());
                Bundle bundle = activityOptions.setLaunchDisplayId(presentationDisplay.getDisplayId()).toBundle();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppApplication.context.startActivity(intent, bundle);
            }


//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//
//            ActivityOptions options = ActivityOptions.makeBasic();
//            options.setLaunchDisplayId(9);
//            Bundle optsBundle = options.toBundle();

//            AppApplication.context.startActivity(intent, optsBundle);
        } catch (Exception e) {
            LogUtil.e(TAG, "createVirtualDisplay error " + e.getMessage());
            e.printStackTrace();
        }
    }*/
}
