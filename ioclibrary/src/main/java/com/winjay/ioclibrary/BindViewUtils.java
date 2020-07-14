package com.winjay.ioclibrary;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BindViewUtils {

    public static void bind(Activity activity) {
        inject(new ViewFinder(activity), activity);
    }

    public static void bind(View view) {
        inject(new ViewFinder(view), view);
    }

    public static void bind(View view, Object object) {
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
            BindView bindViewById = field.getAnnotation(BindView.class);
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

    /**
     * 事件注入
     */
    private static void injectEvent(ViewFinder viewFinder, Object object) {
        // 1.获取类里面所有的方法
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        // 2.获取OnClick的里面的value
        for (Method method : methods) {
            OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick != null) {
                int[] viewIds = onClick.value();
                for (int viewId : viewIds) {
                    // 3.findViewById找到View
                    View view = viewFinder.findViewById(viewId);

                    // 扩展功能：网络检测
                    boolean isCheckNet = method.getAnnotation(CheckNet.class) != null;
                    // 扩展功能：防暴力点击
                    NoDoubleClick noDoubleClick = method.getAnnotation(NoDoubleClick.class);
                    int delayTime = 0;
                    if (noDoubleClick != null) {
                        delayTime = noDoubleClick.value();
                    }

                    if (view != null) {
                        view.setOnClickListener(new DeclaredOnClickListener(method, object, isCheckNet, delayTime));
                    }
                }
            }
        }
    }

    private static class DeclaredOnClickListener extends NoDoubleClickListener {
        private Object mObject;
        private Method mMethod;
        private boolean mIsCheckNet;

        public DeclaredOnClickListener(Method method, Object object, boolean isCheckNet, long delayTime) {
            super(delayTime);
            this.mMethod = method;
            this.mObject = object;
            this.mIsCheckNet = isCheckNet;
        }

        @Override
        protected void onNoDoubleClick(View v) {
            if (mIsCheckNet) {
                if (!networkAvailable(v.getContext())) {
                    // TODO 提示语需要可配置
                    Toast.makeText(v.getContext(), "网络连接失败，请检查网络！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            try {
                // 所有方法都可以，包括私有公有
                mMethod.setAccessible(true);
                // 5.反射执行方法
                mMethod.invoke(mObject, v);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    mMethod.invoke(mObject, null);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 网络检测
     *
     * @param context
     * @return
     */
    private static boolean networkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
