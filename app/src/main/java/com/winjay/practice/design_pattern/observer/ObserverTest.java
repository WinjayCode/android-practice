package com.winjay.practice.design_pattern.observer;

/**
 * 定义：对象间一种一对多的依赖关系，使得当一个对象改变状态，则所有依赖于它的对象都会得到通知并被自动更新。
 * <p>
 * 运用的场景：广播，EventBus等都是观察者模式
 *
 * 观察者模式的主要组成部分：
 * 主题（Subject）：
 * 也被称为“Observable”，它维护了一系列观察者对象，并在自身状态发生变化时通知它们。
 *
 * 观察者（Observer）：
 * 定义了观察者对象的接口，包括更新的方法 update()，当接收到主题对象的更改通知时被调用。
 *
 * 具体主题（Concrete Subject）：
 * 也被称为“具体Observable”，实现抽象主题的接口，存储状态，并提供注册和移除观察者的方法。
 *
 * 具体观察者（Concrete Observer）：
 * 实现观察者接口，根据主题对象的状态变化执行具体的动作。
 *
 * 观察者模式的工作流程：
 * 注册观察者：
 * 观察者对象向主题对象注册自己。
 *
 * 状态变更：
 * 当主题对象的状态发生变化时。
 *
 * 通知观察者：
 * 主题对象将状态变更通知给所有注册的观察者。
 *
 * 更新观察者：
 * 观察者接收到通知后，调用自己的 update() 方法进行相应的处理。
 *
 * @author Winjay
 * @date 2023-03-02
 */
public class ObserverTest {
    public static void main(String[] args) {
        ConcreteSubject subject = new ConcreteSubject();
        ConcreteObserver observer1 = new ConcreteObserver("Observer 1");
        ConcreteObserver observer2 = new ConcreteObserver("Observer 2");

        subject.registerObserver(observer1);
        subject.registerObserver(observer2);

        subject.setState("new state"); // 通知所有观察者
    }
}
