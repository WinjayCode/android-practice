package com.winjay.practice.design_pattern.adapter;

/**
 * 接口的适配器模式
 *
 * @author Winjay
 * @date 2023-03-17
 */
public class InterfaceAdapter {
    public static abstract class InterfaceWrapper implements AdapterTargetable {

        @Override
        public void method1() {

        }

        @Override
        public void method2() {

        }
    }

    public static class Wrapper extends InterfaceWrapper {
        @Override
        public void method2() {
            System.out.println("this is InterfaceAdapter method!");
        }
    }

    public static void main(String[] args) {
        Wrapper wrapper = new Wrapper();
        wrapper.method2();
    }
}
