package com.winjay.practice.test_java;

import com.winjay.practice.utils.LogUtil;

public class Test {


    public static void main(String args[]) {
        B b = new B();
        b.a();
    }

    static class B extends A {
        private final String TAG = getClass().getSimpleName();

        @Override
        protected void a() {
            super.a();
//            LogUtil.d(TAG, "222");
            System.out.println(TAG);
        }
    }

    static abstract class A {
        private final String TAG = getClass().getSimpleName();

        protected void a() {
//            LogUtil.d(TAG, "111");
            System.out.println(TAG);
        }
    }
}
