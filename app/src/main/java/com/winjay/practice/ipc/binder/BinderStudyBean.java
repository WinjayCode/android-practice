package com.winjay.practice.ipc.binder;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * @author Winjay
 * @date 2022-10-20
 */
public class BinderStudyBean implements Parcelable {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public BinderStudyBean(String name, int age) {
        this.name = name;
        this.age = age;
    }

    protected BinderStudyBean(Parcel in) {
        name = in.readString();
        age = in.readInt();
    }

    public static final Creator<BinderStudyBean> CREATOR = new Creator<BinderStudyBean>() {
        @Override
        public BinderStudyBean createFromParcel(Parcel in) {
            return new BinderStudyBean(in);
        }

        @Override
        public BinderStudyBean[] newArray(int size) {
            return new BinderStudyBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(age);
    }
}
