package com.winjay.practice.design_pattern.adapter;

/**
 * 适配器模式将某个类的接口转换成客户端期望的另一个接口表示，目的是消除由于接口不匹配所造成的类的兼容性问题。
 * <p>
 * 主要分为三类：类的适配器模式、对象的适配器模式、接口的适配器模式。
 *
 * @author Winjay
 * @date 2023-03-17
 */
public class AdapterSource {
    public void method1() {
        System.out.println("this is original method!");
    }
}
