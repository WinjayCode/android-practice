package com.winjay.practice.design_pattern.observer;

/**
 * 具体观察者
 *
 * @author Winjay
 * @date 2024-05-20
 */
public class ConcreteObserver implements Observer {
    private String name;

    public ConcreteObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + " received update: " + message);
    }
}
