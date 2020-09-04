package com.winjay.practice.thread_communication;

public class Producer implements Runnable {
    //获得面包的类
    private Breads bre;

    //无参构造
    public Producer() {
        super();
    }

    //有参构造
    public Producer(Breads bre) {
        super();
        this.bre = bre;
    }

    //继承重写run方法
    @Override
    public void run() {
        pro();
    }

    //生产面包
    private void pro() {
        // 本系统默认循环生产20个面包
        for (int i = 0; i < 20; i++) {
            try {
                //沉睡0.3秒（演示效果需要，可以不加）
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //调用面包类里的生产面包的方法
            bre.produc();
        }
    }
}
