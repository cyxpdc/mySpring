package com.pdc.spring.advisor;

import java.lang.reflect.Method;

public interface Advisor {
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable;

    default public boolean intercept(Class<?> cls, Method method, Object[] params) throws Throwable {
        return true;
    }

    public void error(Class<?> cls, Method method, Object[] params, Throwable e) ;

    public void begin();

    public void end();

    public void after(Class<?> cls, Method method, Object[] params);
}
