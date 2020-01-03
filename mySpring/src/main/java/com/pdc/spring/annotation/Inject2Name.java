package com.pdc.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)// 字段、枚举的常量
@Retention(RetentionPolicy.RUNTIME)// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
public @interface Inject2Name {
    /**
     * 实例化类的名字
     * @return
     */
    String name();
}
