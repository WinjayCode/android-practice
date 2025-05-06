package com.winjay.practice.plugin;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * 插件化
 * <p>
 * 把PluginTest.java编译成PluginTest.class，在将PluginTest.class编译成PluginTest.dex
 *
 * @author Winjay
 * @date 2021-02-19
 */
public class PluginActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.plugin_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.load_plugin_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlugin();
            }
        });
        findViewById(R.id.merge_plugin_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mergeDex();
            }
        });
    }

    void loadPlugin() {
        //在android28（9.0）虚拟机上测试成功
        PathClassLoader pathClassLoader = new PathClassLoader("/sdcard/PluginTest.dex", null);
        try {
            Class<?> clazz = pathClassLoader.loadClass("com.winjay.practice.plugin.PluginTest");
            Method method = clazz.getMethod("print");
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void mergeDex() {
        loadClass(this, "/sdcard/PluginTest.dex");
        try {
            Class<?> clazz = Class.forName("com.winjay.practice.plugin.PluginTest");
            Method method = clazz.getMethod("print");
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadClass(Context context, String apkPath) {
        //合并dexElements
        try {
            //获取BaseDexClassLoader中的pathList（DexPathList）
            Class<?> clazz = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField = clazz.getDeclaredField("pathList");
            pathListField.setAccessible(true);

            //获取DexPathList中的dexElements数组
            Class<?> dexPathListClass = Class.forName("dalvik.system.DexPathList");
            Field dexElementsField = dexPathListClass.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);

            //宿主的类加载器
            ClassLoader pathClassLoader = context.getClassLoader();
            //DexPathList类的对象
            Object hostPathList = pathListField.get(pathClassLoader);
            //宿主的dexElements
            Object[] hostDexElements = (Object[]) dexElementsField.get(hostPathList);

            //插件的类加载器
            ClassLoader dexClassLoader = new DexClassLoader(apkPath
                    , context.getCacheDir().getAbsolutePath()
                    , null
                    , pathClassLoader);

            //DexPathList类的对象
            Object pluginPathList = pathListField.get(dexClassLoader);
            //宿主的dexElements
            Object[] pluginElements = (Object[]) dexElementsField.get(pluginPathList);

            //创建一个新数组
            Object[] newDexElements = (Object[]) Array.newInstance(hostDexElements.getClass().getComponentType()
                    , hostDexElements.length + pluginElements.length);
            System.arraycopy(hostDexElements, 0, newDexElements, 0, hostDexElements.length);
            System.arraycopy(pluginElements, 0, newDexElements, hostDexElements.length, pluginElements.length);
            //赋值
            dexElementsField.set(hostPathList, newDexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
