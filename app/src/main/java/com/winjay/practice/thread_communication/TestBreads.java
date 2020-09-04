package com.winjay.practice.thread_communication;

/**
 * 线程间通讯
 *
 * 一、为什么要线程通信？
 *
 * 1. 多个线程并发执行时, 在默认情况下CPU是随机切换线程的，当我们需要多个线程来共同完成一件任务，
 *
 * 　　 并且我们希望他们有规律的执行, 那么多线程之间需要一些协调通信，以此来帮我们达到多线程共同操作一份数据。
 *
 * 2.当然如果我们没有使用线程通信来使用多线程共同操作同一份数据的话，虽然可以实现，
 *
 * 　　但是在很大程度会造成多线程之间对同一共享变量的争夺，那样的话势必为造成很多错误和损失！
 *
 * 3.所以，我们才引出了线程之间的通信，多线程之间的通信能够避免对同一共享变量的争夺。
 *
 * 二、什么是线程通信？
 *
 * 　　多个线程在处理同一个资源，并且任务不同时，需要线程通信来帮助解决线程之间对同一个变量的使用或操作。
 *
 * 　    就是多个线程在操作同一份数据时， 避免对同一共享变量的争夺。
 *
 * 　　于是我们引出了等待唤醒机制：（wait()、notify()）
 *
 * 　　就是在一个线程进行了规定操作后，就进入等待状态（wait）， 等待其他线程执行完他们的指定代码过后 再将其唤醒（notify）；
 *
 * 1）wait()方法：
 * 　　  线程调用wait()方法，释放它对锁的拥有权，同时他会在等待的位置加一个标志，为了以后使用notify()或者notifyAll()方法  唤醒它时，它好能从当前位置获得锁的拥有权，变成就绪状态，
 *
 * 　　  要确保调用wait()方法的时候拥有锁，即，wait()方法的调用必须放在synchronized方法或synchronized块中。  在哪里等待被唤醒时，就在那里开始执行。
 *
 * 1）notify/notifyAll()方法：
 * 　　notif()方法：notify()方法会唤醒一个等待当前对象的锁的线程。唤醒在此对象监视器上等待的单个线程。
 *
 * 　notifAll()方法： notifyAll（）方法会唤醒在此对象监视器上等待的所有线程。
 *
 *  当执行notify/notifyAll方法时，会唤醒一个处于等待该 对象锁 的线程，然后继续往下执行，直到执行完退出对象锁锁住的区域（synchronized修饰的代码块）后再释放锁。
 *
 * 从这里可以看出，notify/notifyAll()执行后，并不立即释放锁，而是要等到执行完临界区中代码后，再释放。故，在实际编程中，我们应该尽量在线程调用notify/notifyAll()后，立即退出临界区。即不要在notify/notifyAll()后面再写一些耗时的代码。
 *
 * @author Winjay
 * @date 2020/9/4
 */
public class TestBreads {
    public static void main(String[] args) {
        //new一个面包类
        Breads bre = new Breads();
        //new一个生产者类
        Producer proth = new Producer(bre);
        //new一个消费者类
        Consume conth = new Consume(bre);
        //new一个包含消费者类的线程
        Thread t1 = new Thread(proth, "生产者");
        //new一个包含生产者类的线程
        Thread t2 = new Thread(conth, "消费者");
        //启动线程
        t1.start();
        t2.start();
    }
}
