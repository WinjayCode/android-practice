package com.winjay.practice.design_pattern.observer;

/**
 * 被观察的数据
 *
 * @author Winjay
 * @date 2023-03-02
 */
public class ObserverData {
    private String data;

    public ObserverData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ObserverData{" +
                "data='" + data + '\'' +
                '}';
    }
}
