package com.winjay.practice.design_pattern.adapter;

/**
 * 类的适配器模式
 *
 * @author Winjay
 * @date 2023-03-17
 */
public class ClassAdapter {

    public static class Wrapper extends AdapterSource implements AdapterTargetable {

        @Override
        public void method2() {
            System.out.println("this is ClassAdapter method!");
        }
    }

    public static void main(String[] args) {
        AdapterTargetable target = new Wrapper();
        target.method1();
        target.method2();
    }
}
