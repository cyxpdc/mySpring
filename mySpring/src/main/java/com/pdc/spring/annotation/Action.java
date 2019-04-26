package com.pdc.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Action 方法注解
 *
 * @author pdc
 */
@Target(ElementType.METHOD)// 方法
@Retention(RetentionPolicy.RUNTIME)// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
public @interface Action {

    /**
     * 请求类型与路径
     */
    String value();
}
