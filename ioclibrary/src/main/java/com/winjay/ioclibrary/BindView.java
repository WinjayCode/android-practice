package com.winjay.ioclibrary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性注解
 *
 * @author Winjay
 * @date 2020/7/12
 */
// ElementType.FIELD 属性、ElementType.TYPE 类、ElementType.CONSTRUCTOR 构造函数、ElementType.METHOD 方法
@Target(ElementType.FIELD)
// 什么时候生效：RetentionPolicy.RUNTIME运行时、RetentionPolicy.CLASS编译时、RetentionPolicy.SOURCE源码资源
@Retention(RetentionPolicy.RUNTIME)
public @interface BindView {
    int value();
}
