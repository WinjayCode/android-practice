package com.winjay.practice.design_pattern.singleton;

/**
 * 静态内部类方式实现单例
 * <p>
 * 缺点：无法传参
 * </p>
 *
 * @author Winjay
 * @date 2020/8/6
 */
public class StaticClassSingleton {
    private StaticClassSingleton() {
    }

    private static class StaticClassSingletonHolder {
        private static final StaticClassSingleton INSTANCE = new StaticClassSingleton();
    }

    public static StaticClassSingleton getInstance() {
        return StaticClassSingletonHolder.INSTANCE;
    }
}
