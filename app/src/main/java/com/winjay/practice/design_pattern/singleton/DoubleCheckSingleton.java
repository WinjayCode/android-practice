package com.winjay.practice.design_pattern.singleton;

/**
 * 双检锁
 *
 * <p>在真正需要使用对象时才去创建该单例类对象</p>
 *
 * <p>如果没有实例化对象则加锁创建，如果已经实例化了，则不需要加锁，直接获取实例</p>
 *
 * <p>使用 volatile 防止指令重排，防止并发情况下出现NPE</p>
 *
 * <p>原因：创建一个对象，在 JVM 中会经过三步：
 * （1）为 singleton 分配内存空间
 * （2）初始化 singleton 对象
 * （3）将 singleton 指向分配好的内存空间</p>
 *
 * <p>
 * volatile作用：
 * （1）使用 volatile 关键字修饰的变量，可以保证其指令执行的顺序与程序指明的顺序一致，不会发生顺序变换
 * （2）使用 volatile 关键字修饰的变量，可以保证其内存可见性，即每一时刻线程读取到该变量的值都是内存中最新的那个值，线程每次操作该变量都需要先读取该变量。
 * （3）不保证原子性!
 * </p>
 *
 * @author Winjay
 * @date 2020/8/6
 */
public class DoubleCheckSingleton {
    private static volatile DoubleCheckSingleton mDoubleCheckSingleton;

    private DoubleCheckSingleton() {
    }

    public static DoubleCheckSingleton getInstance() {
        if (mDoubleCheckSingleton == null) {
            synchronized (DoubleCheckSingleton.class) {
                if (mDoubleCheckSingleton == null) {
                    mDoubleCheckSingleton = new DoubleCheckSingleton();
                }
            }
        }
        return mDoubleCheckSingleton;
    }
}
