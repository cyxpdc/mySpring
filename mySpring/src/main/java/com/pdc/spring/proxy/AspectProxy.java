package com.pdc.spring.proxy;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pdc.spring.advisor.Advice;
import com.pdc.spring.advisor.impl.DefaultAdvice;

/**
 * 切面代理，即代理对象
 * @author pdc
 */
public abstract class AspectProxy implements Proxy {

    private static final Logger logger = LoggerFactory.getLogger(AspectProxy.class);

    private Advice advice = new DefaultAdvice();

    @Override
    public final Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result = null;

        Class<?> cls = proxyChain.getTargetClass();//获得目标类
        Method method = proxyChain.getTargetMethod();//获得目标方法
        Object[] params = proxyChain.getMethodParams();//获得目标方法参数

        begin();
        //调用框架
        try {
            if (advice.intercept(cls, method, params)) {
                before(cls, method, params);
                result = proxyChain.doProxyChain();//执行代理和目标对象
                after(cls, method, params, result);
            } else {
                result = proxyChain.doProxyChain();
            }
        } catch (Exception e) {
            logger.error("proxy failure", e);
            error(cls, method, params, e);
            throw e;
        } finally {
            end();
        }
        return result;
    }

    public void begin() {
        advice.begin();
    }

    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        advice.before(cls, method, params);
    }

    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
        advice.after(cls, method, params);
    }

    public void error(Class<?> cls, Method method, Object[] params, Throwable e) {
        advice.error(cls, method, params,e);
    }

    public void end() {
        advice.end();
    }
}