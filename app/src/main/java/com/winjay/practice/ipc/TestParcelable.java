package com.winjay.practice.ipc;

import android.os.Parcel;
import android.os.Parcelable;

import com.winjay.practice.aidl.Book;

/**
 * Parcelable序列化和反序列化
 * <p>
 * 序列化功能由writeToParcel方法来完成，最终是通过Parcel中的一系列write方法来完成的。
 * 反序列化功能由CREATOR来完成，其内部标明了如何创建序列化对象和数组，并通过Parcel的一
 * 系列read方法来完成反序列化过程。
 *
 * 优点：
 * 效率高。
 *
 * 缺点：
 * 使用复杂。
 *
 * @author Winjay
 * @date 2022-10-16
 */
public class TestParcelable implements Parcelable {
    public int userId;
    public String userName;
    public boolean isMale;

    public Book book;

    public TestParcelable(int userId, String userName, boolean isMale) {
        this.userId = userId;
        this.userName = userName;
        this.isMale = isMale;
    }

    // 反序列化过程
    // 从序列化后的对象中创建原始对象
    protected TestParcelable(Parcel in) {
        userId = in.readInt();
        userName = in.readString();
        isMale = in.readByte() != 0;
        // book是另一个可序列化对象，所以它的反序列化过程需要传递当前线程的上下文类加载器，否则会报无法找到类的错误
        book = in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<TestParcelable> CREATOR = new Creator<TestParcelable>() {
        // 从序列化后的对象中创建原始对象
        @Override
        public TestParcelable createFromParcel(Parcel in) {
            return new TestParcelable(in);
        }

        // 创建指定长度的原始对象数组
        @Override
        public TestParcelable[] newArray(int size) {
            return new TestParcelable[size];
        }
    };

    // 返回当前对象的内容描述。如果含有文件描述符，返回1，否则返回0，几乎所有情况都为0。
    @Override
    public int describeContents() {
        return 0;
    }

    // 序列化过程
    // 将当前对象写入序列化结构中，其中flags标识有两种值：0或者1。
    // 为 1 时标识当前对象需要作为返回值返回，不能立即释放资源，几乎所有情况都为0。
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeByte((byte) (isMale ? 1 : 0));
        dest.writeParcelable(book, 0);
    }
}
