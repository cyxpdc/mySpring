package com.pdc.spring.advisor.impl;

import com.pdc.spring.advisor.Advisor;

import java.lang.reflect.Method;

/**
 * author PDC
 */
public class DefaultAdvisor implements Advisor {

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        System.out.println("before");
    }

    @Override
    public void error(Class<?> cls, Method method, Object[] params, Throwable e) {
        System.out.println("error");
    }

    @Override
    public void begin() {
        System.out.println("begin");
    }

    @Override
    public void end() {
        System.out.println("end");
    }

    @Override
    public void after(Class<?> cls, Method method, Object[] params) {
        System.out.println("after");
    }
}
