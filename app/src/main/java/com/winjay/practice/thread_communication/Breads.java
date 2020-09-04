package com.winjay.practice.thread_communication;

public class Breads {
    //面包的id
    private int bid;
    //面包的个数
    private int num;

    //生产面包的方法（由于是demo，方便大家理解，就把synchronized关键字加到方法上面了哦）
    public synchronized void produc() {
        //当面包的数量不为0时，该方法处于等待状态
        //多对多的时候 while
        if (0 != num) {
            try {
                //等待
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //当面包数量为0时，那么就开始生产面包了哦
        //数量加1
        num = num + 1;
        //id当然也得加1
        bid = bid + 1;
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + "生产了一个编号为" + bid + "的面包！个数为" + num);
        //当执行完后，去唤醒其他处于等待的线程
        notify();
        //多对多的时候 notifyAll()
    }

    //消费面包的方法
    public synchronized void consume() {
        //当面包的数量为0时，该方法处于等待状态
        //多对多的时候  while
        if (num == 0) {
            try {
                //等待
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //消费完面包了，所以面包数量降为0了
        //数量减1
        num = num - 1;
        String name1 = Thread.currentThread().getName();
        System.out.println(name1 + "买了一个面包编号为" + bid + "个数为" + num);
        //当执行完后，去唤醒其他处于等待的线程
        notify();
        //多对多的时候notifyAll()
    }

    //有参构造
    public Breads(int bid, int num) {
        super();
        this.bid = bid;
        this.num = num;
    }

    //无参构造
    public Breads() {
        super();
        // TODO Auto-generated constructor stub
    }
}
