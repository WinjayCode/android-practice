package com.winjay.scrcpy.wrappers;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.SurfaceTexture;
import android.os.IInterface;
import android.util.DisplayMetrics;
import android.view.Surface;

import com.winjay.scrcpy.DisplayInfo;
import com.winjay.scrcpy.Ln;
import com.winjay.scrcpy.Size;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public final class DisplayManager {
    private final IInterface manager;

    public DisplayManager(IInterface manager) {
        this.manager = manager;
    }

    public DisplayInfo getDisplayInfo(int displayId) {
        try {
            Object displayInfo = manager.getClass().getMethod("getDisplayInfo", int.class).invoke(manager, displayId);
            if (displayInfo == null) {
                return null;
            }
            Class<?> cls = displayInfo.getClass();
            // width and height already take the rotation into account
            int width = cls.getDeclaredField("logicalWidth").getInt(displayInfo);
            int height = cls.getDeclaredField("logicalHeight").getInt(displayInfo);
            int rotation = cls.getDeclaredField("rotation").getInt(displayInfo);
            int layerStack = cls.getDeclaredField("layerStack").getInt(displayInfo);
            int flags = cls.getDeclaredField("flags").getInt(displayInfo);
            return new DisplayInfo(displayId, new Size(width, height), rotation, layerStack, flags);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public int[] getDisplayIds() {
        try {
            return (int[]) manager.getClass().getMethod("getDisplayIds").invoke(manager);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }


//    public int createVirtualDisplay() {
//        try {
//            SurfaceTexture surfaceTexture = new SurfaceTexture(10);
//            Surface surface = new Surface(surfaceTexture);
//            Object virtualDisplay = manager.getClass().getMethod("createVirtualDisplay", String.class, int.class, int.class, int.class, Surface.class, int.class).invoke(manager, "test_vd", 400, 400, 360, surface, 139);
//            if (virtualDisplay == null) {
//                Ln.i("virtualDisplay == null");
//                return 0;
//            }
//            Class<?> cls = virtualDisplay.getClass();
//            int displayId = cls.getDeclaredField("mDisplayId").getInt(virtualDisplay);
//            Ln.i("virtualDisplayId = " + displayId);
//            return displayId;
//        } catch (Exception e) {
//            Ln.i("createVirtualDisplay error " + e.getMessage());
//            e.printStackTrace();
//            return 0;
//        }
//    }

    private static Context getContextWithoutActivity() {
        try {
//            Class<?> cls = Class.forName("android.app.ActivityThread");
//            Constructor<?> declaredConstructor = cls.getDeclaredConstructor(new Class[0]);
//            declaredConstructor.setAccessible(true);
//            return (Context) cls.getDeclaredMethod("getSystemContext", new Class[0]).invoke(declaredConstructor.newInstance(new Object[0]), new Object[0]);


            Class<?> cls = Class.forName("android.app.ActivityThread");
            Constructor<?> declaredConstructor = cls.getDeclaredConstructor(new Class[0]);
            declaredConstructor.setAccessible(true);
            Object newInstance = declaredConstructor.newInstance(new Object[0]);
            Field declaredField = cls.getDeclaredField("sCurrentActivityThread");
            declaredField.setAccessible(true);
            declaredField.set(null, newInstance);
            Class<?> cls2 = Class.forName("android.app.ActivityThread$AppBindData");
            Constructor<?> declaredConstructor2 = cls2.getDeclaredConstructor(new Class[0]);
            declaredConstructor2.setAccessible(true);
            Object newInstance2 = declaredConstructor2.newInstance(new Object[0]);
            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.packageName = ServiceManager.PACKAGE_NAME;
            Field declaredField2 = cls2.getDeclaredField("appInfo");
            declaredField2.setAccessible(true);
            declaredField2.set(newInstance2, applicationInfo);
            Field declaredField3 = cls.getDeclaredField("mBoundApplication");
            declaredField3.setAccessible(true);
            declaredField3.set(newInstance, newInstance2);
            Context context2 = (Context) cls.getDeclaredMethod("getSystemContext", new Class[0]).invoke(newInstance, new Object[0]);
            Application newApplication = Instrumentation.newApplication(Application.class, context2);
            Field declaredField4 = cls.getDeclaredField("mInitialApplication");
            declaredField4.setAccessible(true);
            declaredField4.set(newInstance, newApplication);
            return context2;
        } catch (Exception e) {
            Ln.i("getContextWithoutActivity error " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void createVirtualDisplay() {
        try {
            Context context = getContextWithoutActivity();
            Ln.i("context name=" + context.getPackageName());
            android.hardware.display.DisplayManager displayManager = (android.hardware.display.DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            SurfaceTexture surfaceTexture = new SurfaceTexture(10);
            Surface surface = new Surface(surfaceTexture);
            int displayId = displayManager.createVirtualDisplay("test-vd", 400, 400, 400, surface, 139).getDisplay().getDisplayId();
            Ln.i("virtualDisplayId = " + displayId);
        } catch (Exception e) {
            Ln.i("createVirtualDisplay error " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void fillAppInfo() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Constructor<?> declaredConstructor = cls.getDeclaredConstructor(new Class[0]);
            declaredConstructor.setAccessible(true);
            Object newInstance = declaredConstructor.newInstance(new Object[0]);
            Field declaredField = cls.getDeclaredField("sCurrentActivityThread");
            declaredField.setAccessible(true);
            declaredField.set(null, newInstance);
            Class<?> cls2 = Class.forName("android.app.ActivityThread$AppBindData");
            Constructor<?> declaredConstructor2 = cls2.getDeclaredConstructor(new Class[0]);
            declaredConstructor2.setAccessible(true);
            Object newInstance2 = declaredConstructor2.newInstance(new Object[0]);
            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.packageName = ServiceManager.PACKAGE_NAME;
            Field declaredField2 = cls2.getDeclaredField("appInfo");
            declaredField2.setAccessible(true);
            declaredField2.set(newInstance2, applicationInfo);
            Field declaredField3 = cls.getDeclaredField("mBoundApplication");
            declaredField3.setAccessible(true);
            declaredField3.set(newInstance, newInstance2);
            Application newApplication = Instrumentation.newApplication(Application.class, (Context) cls.getDeclaredMethod("getSystemContext", new Class[0]).invoke(newInstance, new Object[0]));
            Field declaredField4 = cls.getDeclaredField("mInitialApplication");
            declaredField4.setAccessible(true);
            declaredField4.set(newInstance, newApplication);
        } catch (Throwable th) {
            Ln.i("Could not fill app info: " + th.getMessage());
        }
    }

    public static int[] getRealMetrics() {
        int[] size = new int[2];
        DisplayMetrics outMetrics = new DisplayMetrics();
        android.view.WindowManager wm = (android.view.WindowManager) getContextWithoutActivity().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(outMetrics);
        size[0] = outMetrics.widthPixels;
        Ln.i("widthPixels=" + size[0]);
        size[1] = outMetrics.heightPixels;
        Ln.i("heightPixels=" + size[1]);
        return size;
    }
}
