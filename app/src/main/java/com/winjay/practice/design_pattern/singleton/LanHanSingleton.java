package com.winjay.practice.design_pattern.singleton;

/**
 * 懒汉式
 * <p>
 * 在真正需要使用对象时才去创建该单例类对象
 * <p>
 * 问题：存在线程安全问题，如果两个线程同时判断 singleton 为空，那么它们都会去实例化一个Singleton 对象，这就变成多例了
 *
 * @author Winjay
 * @date 2020/8/6
 */
public class LanHanSingleton {
    private static LanHanSingleton mLanHanSingleton;

    private LanHanSingleton() {
    }

    public static LanHanSingleton getInstance() {
        if (mLanHanSingleton == null) {
            mLanHanSingleton = new LanHanSingleton();
        }
        return mLanHanSingleton;
    }
}
