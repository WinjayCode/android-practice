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

//        System.out.println(secondToTime(800));

//        int ten = 91800 / 10;
//        String sixteen = Integer.toHexString(ten);
//        System.out.println(sixteen);
//        byte[] a = hexToByteArray(sixteen);
//        for(int i = 0; i < a.length; i++) {
//            System.out.println(a[i] & 0xFF);
//        }
//        System.out.println(Arrays.toString(hexToByteArray(sixteen)));
//        System.out.println(bytesToHex(a));
    }

    public static String secondToTime(long second) {
        long days = second / 86400;//转换天数
        second = second % 86400;//剩余秒数
        long hours = second / 3600;//转换小时数
        second = second % 3600;//剩余秒数
        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数
        if (0 < days) {
            return days + ":" + hours + ":" + minutes + ":" + second;
        } else {
            return hours + ":" + minutes + ":" + second;
        }
    }

    static class B extends A {
        private final String TAG = getClass().getSimpleName();

        @Override
        protected void a() {
//            super.a();
            System.out.println("222");
            System.out.println(TAG);
        }
    }

    static abstract class A {
        private final String TAG = getClass().getSimpleName();

        protected void a() {
            System.out.println("111");
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
    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * Hex字符串转byte
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }

    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return  转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
