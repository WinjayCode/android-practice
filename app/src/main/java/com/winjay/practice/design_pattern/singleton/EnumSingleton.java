package com.winjay.practice.design_pattern.singleton;

/**
 * 枚举类型的单例模式
 * <p>
 * 优势：
 * （1）一目了然的代码
 * （2）天然的线程安全与单一实例
 * （3）枚举保护单例模式不被破坏
 * <p/>
 *
 * <p>
 * 简单地理解枚举创建实例的过程：在程序启动时，会调用 Singleton 的空参构造器，实例化好一个Singleton 对象赋给 INSTANCE，之后再也不会实例化
 * </p>
 *
 * @author Winjay
 * @date 2020/8/6
 */
public enum EnumSingleton {
    INSTANCE;

    EnumSingleton() {
        System.out.println("枚举创建对象了");
    }

    public void doSomething() {
        System.out.println("这是枚举类型的单例模式！");
    }
}
