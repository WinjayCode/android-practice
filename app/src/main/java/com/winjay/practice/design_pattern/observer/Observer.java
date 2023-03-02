package com.winjay.practice.design_pattern.observer;

/**
 * 观察者
 *
 * @author Winjay
 * @date 2023-03-02
 */
public interface Observer<T> {
    void onUpdate(T data);
}
