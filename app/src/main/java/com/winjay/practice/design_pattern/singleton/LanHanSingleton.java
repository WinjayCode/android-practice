package com.winjay.practice.design_pattern.singleton;

/**
 * 懒汉式（线程安全）：直到第一次调用 getInstance() 方法时才创建实例，并使用 synchronized 关键字确保线程安全。
 * 使用 synchronized 关键字确保线程安全，但效率较低，因为每次访问都要进行同步。
 *
 * @author Winjay
 * @date 2020/8/6
 */
public class LanHanSingleton {
    private static LanHanSingleton mLanHanSingleton;

    private LanHanSingleton() {
    }

    public static synchronized LanHanSingleton getInstance() {
        if (mLanHanSingleton == null) {
            mLanHanSingleton = new LanHanSingleton();
        }
        return mLanHanSingleton;
    }
}
