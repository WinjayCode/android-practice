package com.winjay.practice.test_java;

import com.winjay.practice.utils.LogUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Test {


    public static void main(String args[]) {
//        B b = new B();
//        b.a();

//        C c = new D();
//        c.c();

        ////////////////////
//        String a = "(ANS)*5÷6";
//        System.out.println(transformExp(a));
        ////////////////////

        ////////////////////
//        DecimalFormat df = new DecimalFormat("##.#####");
//        float f1 = 11115.55f;
//        float f2 = 1111115.0000f;
//        System.out.println(df.format(f1));
//        System.out.println(df.format(f2));
        ////////////////////

        ////////////////////
//        float f = 11115.55f;
//        int fi=(int)f;
//        if(f==fi)
//            System.out.println(fi);
//        else
//            System.out.println(f);
        ////////////////////


        ////////////////////
//        String a = "xiaotiaowu.mp3";
//        String[] b = a.split("\\.");
//        System.out.println(b[0]);
        ////////////////////


        ////////////////////
//        String Str = new String("www.runoob.com");
//
//        System.out.print("返回值 :" );
//        System.out.println(Str.substring(4) );
//
//        System.out.print("返回值 :" );
//        System.out.println(Str.substring(0, 14));
        ////////////////////

        ////////////////////
        // 取余
//        for (int i = 0; i < 14; i++) {
//            boolean isCurrentLineFinish = (i % 7 == 0);
//            System.out.println(isCurrentLineFinish);
//        }
        ////////////////////

        ////////////////////
//        ArrayList<String> listA = new ArrayList<>(Arrays.asList("1", "2"));
//        String[] listB = new String[]{"3", "4"};
//        listA.addAll(Arrays.asList(listB));
//        System.out.println(listA.toString());
        ////////////////////

//        System.out.println(UUID.randomUUID().toString());
//        System.out.println(UUID.randomUUID().toString());

//        int[] a = new int[]{110, 111};
//        for (int i : a) {
//            System.out.println(i);
//        }
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

    public interface C {
        void c();
    }

    public static class D implements C {

        @Override
        public void c() {
            System.out.println("cccc");
        }
    }


    ////////////////////////
    public static String transformExp(String exp) {
        if (exp.startsWith("(ANS)")) {
            exp = exp.replace("(ANS)", "AAA");
        }
        exp = exp.replace("*", "×");
        exp = exp.replace("/", "÷");
        return exp;
    }
    ////////////////////////
}
