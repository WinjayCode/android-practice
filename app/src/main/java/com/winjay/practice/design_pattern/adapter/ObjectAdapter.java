package com.winjay.practice.design_pattern.adapter;

/**
 * 对象的适配器模式
 *
 * @author Winjay
 * @date 2023-03-17
 */
public class ObjectAdapter {

    public static class Wrapper implements AdapterTargetable {
        private AdapterSource adapterSource;

        public Wrapper(AdapterSource adapterSource) {
            this.adapterSource = adapterSource;
        }

        @Override
        public void method1() {
            adapterSource.method1();
        }

        @Override
        public void method2() {
            System.out.println("this is ObjectAdapter method!");
        }
    }

    public static void main(String[] args) {
        AdapterSource adapterSource = new AdapterSource();
        AdapterTargetable adapterTargetable = new Wrapper(adapterSource);
        adapterTargetable.method1();
        adapterTargetable.method2();
    }
}
