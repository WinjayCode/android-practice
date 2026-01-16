package com.winjay.practice.java;

/**
 * 泛型学习
 * <p>
 * 泛型的好处就是在编译的时候能够检查类型安全，并且所有的强制转换都是自动和隐式的
 * 常用的 T，E，K，V，？
 * ？表示不确定的 java 类型
 * T (type) 表示具体的一个java类型
 * K V (key value) 分别代表java键值中的Key Value
 * E (element) 代表Element
 * 上界通配符 < ? extends E>
 *     上界：用 extends 关键字声明，表示参数化的类型可能是所指定的类型，或者是此类型的子类。
 * 下界通配符 < ? super E>
 *     下界: 用 super 进行声明，表示参数化的类型可能是所指定的类型，或者是此类型的父类型，直至 Object
 * ？和 T 的区别
 * T 是一个 确定的 类型，通常用于泛型类和泛型方法的定义，？是一个 不确定 的类型，通常用于泛型方法的调用代码和形参，不能用于定义类和泛型方法。
 * 区别1：通过 T 来 确保 泛型参数的一致性
 * 区别2：类型参数可以多重限定而通配符不行
 * 区别3：通配符可以使用超类限定而类型参数不行（T extends A/? extends A、? super A）
 *
 * @author winjay
 * @date 2019-08-28
 */
public class GenericityJava<T> {
    private T t;

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }

    public static void main(String args[]) {

    }

    /**
     * 不指定类型
     */
    public void noSpecifyType() {
        GenericityJava genericityJava = new GenericityJava();
        genericityJava.set("test");
        // 需要强制类型转换
        String test = (String) genericityJava.get();
        System.out.println(test);
    }

    /**
     * 指定类型
     */
    public void specifyType() {
        GenericityJava<String> genericityJava = new GenericityJava<>();
        genericityJava.set("test");
        // 不需要强制类型转换
        String test = genericityJava.get();
        System.out.println(test);
    }
}
