package com.pdc.spring.advisor;

import java.lang.reflect.Method;

public interface Advice {
    void before(Class<?> cls, Method method, Object[] params) throws Throwable;

    default boolean intercept(Class<?> cls, Method method, Object[] params) throws Throwable {
        return true;
    }

    void error(Class<?> cls, Method method, Object[] params, Throwable e) ;

    void begin();

    void end();

    void after(Class<?> cls, Method method, Object[] params);
}
