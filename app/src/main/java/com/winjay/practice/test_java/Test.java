package com.winjay.practice.test_java;

import android.os.Build;
import android.os.Environment;
import android.os.HandlerThread;
import android.util.Log;

import com.winjay.practice.utils.FileUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
//        String a = "1_yutong.mp4";
//        String[] b = a.split("_");
//        System.out.println(b[0]); // 1
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

//        byte[] channel = new byte[2];
//        channel[0] = 0x01;
//        channel[1] = 0x40;
////        System.out.println(Integer.parseInt(bytesToHex(channel), 16));
//
//        int ten = Integer.parseInt(bytesToHex(channel), 16);
//        String radioQualityCondition = new DecimalFormat("##.#").format((double) ten / 10);
//        System.out.println(radioQualityCondition);


//        System.out.println(Integer.parseInt("38", 16) + 56);

//        byte[] value = new byte[3];
//        value[0] = 0x1;
//        value[1] = 0x63;
//        value[2] = 0x31;
//
//        int speedLow = value[2] & 0xFF;
//        System.out.println(speedLow);
//        int speedHigh = value[1] & 0xFF;
//        System.out.println(speedHigh);
//        int mCarSpeed1 = (int) ((speedLow | (speedHigh << 8)) * 0.0313);
//        System.out.println(mCarSpeed1);
//
//        byte[] speed = new byte[2];
//        System.arraycopy(value, 1, speed, 0, 2);
//        int mCarSpeed2 = (int) (Integer.valueOf(bytesToInt(speed)) * 0.0313);
//        System.out.println(mCarSpeed2);

//        System.out.println(Integer.parseInt("21700803", 16));

//        byte a = 0x35;
//        System.out.println(byteTobit(a));
//
//        System.out.println(Integer.toBinaryString(0x35));


//        log();

//        int checkValue = 0;
//        int type = 0;
//        checkValue |= 1 << type;
//        int result = checkValue ^ 127;
//        System.out.println(result);


//        System.out.println("isOnline=" + isOnline());
//        System.out.println("aaa=" + (0x1 << 1));

//        System.out.println(Arrays.toString(hexToByteArray("FF")));

        // string互转byte[]
//        String str = "hello";
//        byte[] strBytes = str.getBytes();
//        String des = new String(strBytes);
//        System.out.println("des=" + des);


//        System.out.println(Arrays.toString(hexToByteArray("8001")));

//        byte[] bytes = new byte[]{0x4C,0x5A,0x59,0x54,0x47,0x47,0x41,0x57,0x39,0x4D,0x31,0x30,0x31,0x38,0x38,0x39,0x33};
//        byte[] bytes = new byte[]{0x32, 0x31,0x48,0x37,0x37,0x42,0x4E,0x2D,0x30,0x30,0x30,0x31};
//        byte[] bytes = new byte[]{0x2f, 0x6f, 0x70, 0x74, 0x2f, 0x6d, 0x32, 0x6d, 0x66, 0x69, 0x6c, 0x65, 0x2f, 0x66, 0x74, 0x70, 0x2f, 0x61, 0x64, 0x73, 0x52, 0x6f, 0x75, 0x74, 0x65, 0x53, 0x74, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x2f, 0x32, 0x30, 0x32, 0x31, 0x30, 0x31, 0x32, 0x30, 0x30, 0x39, 0x31, 0x33, 0x35, 0x30};
//        byte[] bytes = new byte[]{0x72, 0x6f, 0x75, 0x74, 0x65, 0x5f, 0x64, 0x61, 0x74, 0x61, 0x2e, 0x6a, 0x73, 0x6f, 0x6e};
//        byte[] bytes = new byte[]{0x77, 0x66, 0x74, 0x70, 0x73, 0x61, 0x66, 0x65};
//        byte[] bytes = new byte[]{0x77, 0x6f, 0x72, 0x6b, 0x32, 0x2e, 0x68, 0x61, 0x72, 0x64};
//        byte[] bytes = new byte[]{0x66, 0x74, 0x70, 0x2e, 0x61, 0x78, 0x78, 0x63, 0x2e, 0x63, 0x6e};
//        byte[] bytes = new byte[]{0x00, 0x15};
//        String re = new String(bytes);
//        System.out.println(re);

//        byte[] bytes = new byte[]{0x66, 0x74, 0x70, 0x2e, 0x61, 0x78, 0x78, 0x63, 0x2e, 0x63, 0x6e};
//        byte[] body = new byte[]{};
//        System.arraycopy(bytes, 3, body, 0, 5);
//        System.out.println(bytesToHex(body));

        // String to bytes
//        String fileDir = "/iCard/icard1s";
//        String fileName = "iCard1S.eng.V1.2.0.ntp.20180608.zip";
//        System.out.println("dir=" + bytesToHex(fileDir.getBytes()));
//        System.out.println("name=" + bytesToHex(fileName.getBytes()));


//        byte[] bytes = new byte[]{
//                0x4c, 0x5a, 0x59, 0x54, 0x47,
//                0x47, 0x41, 0x57, 0x35, 0x4d,
//                0x31, 0x30, 0x31, 0x38, 0x38,
//                0x37, 0x34, 0x09, 0x32, 0x31,
//                0x48, 0x37, 0x37, 0x42, 0x4e,
//                0x2d, 0x30, 0x30, 0x30, 0x33};
//        String re = new String(bytes);
//        System.out.println(re);

//        String re = String.valueOf(96000 / 1000f);
//        System.out.println(re);
//        System.out.println(4200 / 20500);

//        List<Integer> list = new ArrayList<>(1);
//        list.add(1);
//        list.add(2);
//        System.out.println(list.size());
    }

    public static boolean isOnline() {
        try {
            new URL("https://www.baidu.com").openStream();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static void log() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        for (StackTraceElement stackTraceElement : trace) {
//            Class<?> clazz = trace[i].getClass();
//            System.out.println(trace[i].getMethodName());
            System.out.println(String.format(Locale.US, "%s: ()", stackTraceElement.getMethodName()));
        }
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
     *
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
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        // 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();

        // 2
//        return new BigInteger(1, bytes).toString(16);

        // 3
        /*StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();*/

        // 4
        /*char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'a','b','c','d','e','f'};
        // 一个字节对应两个16进制数，所以长度为字节数组乘2
        char[] resultCharArray = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) {
            resultCharArray[index++] = hexDigits[b>>>4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);*/
    }

    public static String bytesToInt(byte[] bytes) {
        return new BigInteger(1, bytes).toString(10);
    }

    public static String byteTobit(byte by) {
        StringBuffer sb = new StringBuffer();
        sb.append((by >> 7) & 0x1)
                .append((by >> 6) & 0x1)
                .append((by >> 5) & 0x1)
                .append((by >> 4) & 0x1)
                .append((by >> 3) & 0x1)
                .append((by >> 2) & 0x1)
                .append((by >> 1) & 0x1)
                .append((by >> 0) & 0x1);
        return sb.toString();
    }
}
