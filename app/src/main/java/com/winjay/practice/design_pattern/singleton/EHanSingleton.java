package com.winjay.practice.design_pattern.singleton;

/**
 * 饿汉式：在类加载时就立即创建实例，这种实现是线程安全的，因为实例的创建是在类初始化时完成的。
 * 在类加载时就立即创建实例，简单且线程安全，但可能提前加载不需要的单例，浪费资源。
 *
 * @author Winjay
 * @date 2020/8/6
 */
public class EHanSingleton {
    private static final EHanSingleton mEHanSingleton = new EHanSingleton();

    private EHanSingleton() {
    }

    public static EHanSingleton getInstance() {
        return mEHanSingleton;
    }
}
