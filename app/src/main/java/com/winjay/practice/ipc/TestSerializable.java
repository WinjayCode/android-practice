package com.winjay.practice.ipc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Serializable序列化和反序列化
 * <p>
 * Java提供的一个序列化接口，它是一个空接口，为对象提供标准的序列化和反序列化操作。
 * <p>
 * 序列化的时候系统会把当前类的serialVersionUID写入序列化的文件中（也可能是其他中介），
 * 当反序列化的时候系统会去检测文件中的serialVersionUID，看它是否和当前类的serialVersionUID
 * 一致，如果一致就说明序列化的类的版本和当前类的版本是相同的，这个时候可以成功反序列化。
 * <p>
 * Tips：
 * 1.静态成员变量属于类不属于对象，所以不会参与序列化过程。
 * 2.用transient关键字标记的成员变量不参与序列化过程。
 *
 * 优点：
 * 使用简单。
 *
 * 缺点：
 * 序列化和反序列化过程需要大量I/O操作。
 *
 * 推荐使用情形：
 * 1.序列化到本地存储设备中
 * 2.序列化后需要通过网络传输
 *
 * @author Winjay
 * @date 2022-10-16
 */
public class TestSerializable implements Serializable {
    private static final long serialVersionUID = 2517094917764504563L;

    public int userId;
    public String userName;
    public boolean isMale;

    public TestSerializable(int userId, String userName, boolean isMale) {
        this.userId = userId;
        this.userName = userName;
        this.isMale = isMale;
    }

    /**
     * 序列化过程
     */
    public static void serializable() {
        TestSerializable testSerializable = new TestSerializable(0, "Winjay", true);
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream("serializable.txt"));
            out.writeObject(testSerializable);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 反序列化过程
     */
    public static void unSerializable() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("serializable.txt"));
            TestSerializable newTestSerializable = (TestSerializable) in.readObject();
            in.close();
            System.out.println(newTestSerializable.userId);
            System.out.println(newTestSerializable.userName);
            System.out.println(newTestSerializable.isMale);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
