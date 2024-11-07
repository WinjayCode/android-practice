package com.winjay.practice.design_pattern.observer;

/**
 * 主题接口
 *
 * @author Winjay
 * @date 2024-05-20
 */
public interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}
