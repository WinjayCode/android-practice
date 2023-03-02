package com.winjay.practice.design_pattern.observer;

/**
 * 定义：对象间一种一对多的依赖关系，使得当一个对象改变状态，则所有依赖于它的对象都会得到通知并被自动更新。
 * <p>
 * 运用的场景：广播，EventBus等都是观察者模式
 *
 * @author Winjay
 * @date 2023-03-02
 */
public class ObserverTest {
    public static void main(String[] args) {
        Observable<ObserverData> observable = new Observable<>();

        Observer<ObserverData> observer1 = new Observer<ObserverData>() {
            @Override
            public void onUpdate(ObserverData data) {
                System.out.println("observer1=" + data);
            }
        };

        Observer<ObserverData> observer2 = new Observer<ObserverData>() {
            @Override
            public void onUpdate(ObserverData data) {
                System.out.println("observer2=" + data);
            }
        };

        observable.registerObserver(observer1);
        observable.registerObserver(observer2);


        ObserverData observerData1 = new ObserverData("111");
        observable.notifyUpdate(observerData1);

        ObserverData observerData2 = new ObserverData("222");
        observable.notifyUpdate(observerData2);

        observable.unregisterObserver(observer1);

        ObserverData observerData3 = new ObserverData("333");
        observable.notifyUpdate(observerData3);
    }
}
