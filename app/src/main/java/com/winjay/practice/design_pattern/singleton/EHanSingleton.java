package com.winjay.practice.design_pattern.singleton;

/**
 * 饿汉式
 * <p>
 * 在类加载时已经创建好该单例对象，等待被程序使用
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
