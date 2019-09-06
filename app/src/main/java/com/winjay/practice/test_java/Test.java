package com.winjay.practice.test_java;

public class Test {
    public static String hello = "天下太的福音";

    public static void main(String args[]) {
//        String[] a = hello.split("太");
//        for (int i = 0; i < a.length; i++) {
//            System.out.println(a[i]);
//        }

        int index = hello.indexOf("太");
        System.out.println(index);

//        String str = "天下太太的福音";
//        int num = 0;
//        for (int i = 0; i < str.length(); i++) {
//            int index = str.substring(i, (i + 1)).indexOf('太');
//            if (index != -1) {
//                System.out.println(i);
//                num = num + 1;
//            }
//        }
//        System.out.println("次数：" + num);
    }
}
