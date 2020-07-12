package com.winjay.baselibrary;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;

public class BindViewUtils {

    public static void inject(Activity activity) {
        inject(new ViewFinder(activity), activity);
    }

    public static void inject(View view) {
        inject(new ViewFinder(view), view);
    }

    public static void inject(View view, Object object) {
        inject(new ViewFinder(view), object);
    }

    /**
     * @param viewFinder
     * @param object     反射需要执行的类
     */
    private static void inject(ViewFinder viewFinder, Object object) {
        injectFiled(viewFinder, object);
        injectEvent(viewFinder, object);
    }

    /**
     * 注入属性
     */
    private static void injectFiled(ViewFinder viewFinder, Object object) {
        // 1.获取类里面所有的属性
        Class<?> clazz = object.getClass();
        // 获取所有属性，包括私有和公有
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            BindViewById bindViewById = field.getAnnotation(BindViewById.class);
            if (bindViewById != null) {
                // 2.获取BindViewById的里面value值（R.id.xxx）
                int viewId = bindViewById.value();
                // 3.findViewById找到View
                View view = viewFinder.findViewById(viewId);
                if (view != null) {
                    // 能够注入所有修饰符 private public
                    field.setAccessible(true);
                    // 4.动态的注入找到的View
                    try {
                        field.set(object, view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void injectEvent(ViewFinder viewFinder, Object object) {
    }

}
