package com.winjay.practice.design_pattern.singleton;

/**
 * 懒汉式
 *
 * <p>在真正需要使用对象时才去创建该单例类对象</p>
 *
 * <p>问题：规避了两个线程同时创建Singleton对象的风险，但是引来另外一个问题：每次去获取对象都需要先获取锁，并发性能非常地差，极端情况下，可能会出现卡顿现象</p>
 *
 * @author Winjay
 * @date 2020/8/6
 */
public class OptimizeLanHanSingleton {
    private static OptimizeLanHanSingleton mLanHanSingleton;

    private OptimizeLanHanSingleton() {
    }

    // 方法加锁
    /*public static synchronized OptimizeLanHanSingleton getInstance() {
        if (mLanHanSingleton == null) {
            mLanHanSingleton = new OptimizeLanHanSingleton();
        }
        return mLanHanSingleton;
    }*/

    // 对象加锁
    public static OptimizeLanHanSingleton getInstance() {
        synchronized (OptimizeLanHanSingleton.class) {
            if (mLanHanSingleton == null) {
                mLanHanSingleton = new OptimizeLanHanSingleton();
            }
        }
        return mLanHanSingleton;
    }
}
