package com.winjay.practice.design_pattern.decorator;

/**
 * 装饰模式就是给一个对象增加一些新的功能，而且是动态的，要求装饰对象和被装饰对象实现同一个接口，
 * 装饰对象持有被装饰对象的实例。
 *
 * @author Winjay
 * @date 2023-03-17
 */
public class DecoratorPattern {
    public interface Sourceable {
        void method();
    }

    public static class Source implements Sourceable {

        @Override
        public void method() {
            System.out.println("this is original method!");
        }
    }

    public static class Decorator implements Sourceable {
        private Sourceable sourceable;

        public Decorator(Sourceable sourceable) {
            this.sourceable = sourceable;
        }

        @Override
        public void method() {
            System.out.println("before decorator!");
            sourceable.method();
            System.out.println("after decorator!");
        }
    }

    public static void main(String[] args) {
        Sourceable source = new Source();
        Sourceable obj = new Decorator(source);
        obj.method();
    }
}
