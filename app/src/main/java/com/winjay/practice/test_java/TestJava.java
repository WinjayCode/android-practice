package com.winjay.practice.test_java;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TestJava {
    private static List<Activity> activityList = new ArrayList<>();

    public static void main(String args[]) {
//        addActivity(null);
//        addActivity(null);
//        removeActivity(null);
//        removeActivity(null);
//        System.out.println("size=" + getActivityCount());

        serializable();
        unSerializable();
    }

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static Activity getTopActivity() {
        if (activityList.size() > 0) {
            return activityList.get(0);
        }
        return null;
    }

    public static int getActivityCount() {
        return activityList.size();
    }

    /**
     * Serializable序列化
     */
    public static class User implements Serializable {
        private static final long serialVersionUID = -266249966500965351L;

        public int userId;
        public String userName;
        public boolean isMale;

        public User(int userId, String userName, boolean isMale) {
            this.userId = userId;
            this.userName = userName;
            this.isMale = isMale;
        }
    }

    /**
     * Parcelable序列化
     */
    public static class User2 implements Parcelable {
        public int userId;
        public String userName;
        public boolean isMale;

        public User2(int userId, String userName, boolean isMale) {
            this.userId = userId;
            this.userName = userName;
            this.isMale = isMale;
        }

        protected User2(Parcel in) {
            userId = in.readInt();
            userName = in.readString();
            isMale = in.readByte() != 0;
        }

        public static final Creator<User2> CREATOR = new Creator<User2>() {
            @Override
            public User2 createFromParcel(Parcel in) {
                return new User2(in);
            }

            @Override
            public User2[] newArray(int size) {
                return new User2[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(userId);
            dest.writeString(userName);
            dest.writeByte((byte) (isMale ? 1 : 0));
        }
    }

    /**
     * 序列化
     */
    public static void serializable() {
        User user = new User(0, "Winjay", true);
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream("serializable.txt"));
            out.writeObject(user);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 反序列化
     */
    public static void unSerializable() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("serializable.txt"));
            User newUser = (User) in.readObject();
            in.close();
            System.out.println(newUser.userId);
            System.out.println(newUser.userName);
            System.out.println(newUser.isMale);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
