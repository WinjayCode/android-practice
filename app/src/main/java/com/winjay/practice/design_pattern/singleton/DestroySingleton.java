package com.winjay.practice.design_pattern.singleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;

/**
 * 利用反射和序列化来破坏单例
 *
 * @author Winjay
 * @date 2020/8/6
 */
public class DestroySingleton {
    public static void main(String[] args) {
//        reflect();
//        serialization();
    }

    /**
     * 反射破坏单例
     */
    private static void reflect() {
        try {
            // 获取类的显式构造器
            Constructor<DoubleCheckSingleton> construct = DoubleCheckSingleton.class.getDeclaredConstructor();
            // 可访问私有构造器
            construct.setAccessible(true);
            // 利用反射构造新对象
            DoubleCheckSingleton obj1 = construct.newInstance();
            // 通过正常方式获取单例对象
            DoubleCheckSingleton obj2 = DoubleCheckSingleton.getInstance();
            // 判断是否是同一个对象(false)
            System.out.println(obj1 == obj2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 序列化破坏单例（需要单例类实现序列化接口）
     */
    private static void serialization() {
        try {
            // 创建输出流
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("DoubleCheckSingleton.file"));
            // 将单例对象写到文件中
            oos.writeObject(DoubleCheckSingleton.getInstance());
            // 从文件中读取单例对象
            File file = new File("DoubleCheckSingleton.file");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            DoubleCheckSingleton newInstance = (DoubleCheckSingleton) ois.readObject();
            // 判断是否是同一个对象(false)
            System.out.println(newInstance == DoubleCheckSingleton.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
