package com.winjay.practice.design_pattern.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 被观察者
 *
 * @author Winjay
 * @date 2023-03-02
 */
public class Observable<T> {
    private List<Observer<T>> observerList = new ArrayList<>();

    public void registerObserver(Observer<T> observer) {
        observerList.add(observer);
    }

    public void unregisterObserver(Observer<T> observer) {
        observerList.remove(observer);
    }

    public void notifyUpdate(T data) {
        for (Observer<T> observer : observerList) {
            observer.onUpdate(data);
        }
    }
}
