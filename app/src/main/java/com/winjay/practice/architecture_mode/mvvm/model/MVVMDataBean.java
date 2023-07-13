package com.winjay.practice.architecture_mode.mvvm.model;

public class MVVMDataBean {
    private String name;
    private int age;

    public MVVMDataBean(String name, int age) {
        this.name = name;
        this.age = age;
    }

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
}
