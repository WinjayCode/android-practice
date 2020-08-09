package com.winjay.bind;

import android.app.Activity;

import java.lang.reflect.Constructor;

/**
 * 注解框架辅助类
 *
 * @author Winjay
 * @date 2020/7/28
 */
public class BindHelper {
    public static Unbinder bind(Activity activity) {
        // xxxActivity_ViewBinding viewBinding = new xxxActivity_ViewBinding(this);
        try {
            Class<? extends Unbinder> bindClassName = (Class<? extends Unbinder>) Class.forName(activity.getClass().getName() + "_ViewBinding");
            // 构造函数
            Constructor<? extends Unbinder> constructor = bindClassName.getDeclaredConstructor(activity.getClass());
            Unbinder unbinder = constructor.newInstance(activity);
            return unbinder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Unbinder.EMPTY;
    }
}
