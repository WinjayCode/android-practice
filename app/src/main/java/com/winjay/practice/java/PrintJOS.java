package com.winjay.practice.java;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程打印奇偶数
 *
 * @author winjay
 * @date 2019-08-08
 */
public class PrintJOS {
    private static boolean flag = true;
    private static CountDownLatch countDownLatch = new CountDownLatch(2);
    private static Integer TOTAL = 100;
    private static AtomicInteger num = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (num.get() <= TOTAL - 1) {
//                    System.out.println("奇数while");
                    if (!flag) {
                        System.out.println(Thread.currentThread().getName() + "：" + num.getAndIncrement());
                        flag = true;
                    }
                }
                countDownLatch.countDown();
            }
        });
        thread1.setName("奇数线程");

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (num.get() <= TOTAL) {
//                    System.out.println("偶数while");
                    if (flag) {
                        System.out.println(Thread.currentThread().getName() + "：" + num.getAndIncrement());
                        flag = false;
                    }
                }
                countDownLatch.countDown();
            }
        });
        thread2.setName("偶数线程");

        thread1.start();
        thread2.start();

        countDownLatch.await();

        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) + "ms");
    }
}
