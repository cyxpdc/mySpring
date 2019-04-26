package com.pdc.spring.proxy;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pdc.spring.advisor.Advisor;
import com.pdc.spring.advisor.impl.DefaultAdvisor;

/**
 * 切面代理，即代理对象
 *
 * @author pdc
 */
public abstract class AspectProxy implements Proxy {

    private static final Logger logger = LoggerFactory.getLogger(AspectProxy.class);

    private Advisor advisor = new DefaultAdvisor();
    /**
     * 执行链式代理
     * @param proxyChain
     * @return
     * @throws Throwable
     */
    @Override
    public final Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result = null;

        Class<?> cls = proxyChain.getTargetClass();//获得目标类
        Method method = proxyChain.getTargetMethod();//获得目标方法
        Object[] params = proxyChain.getMethodParams();//获得目标方法参数

        begin();
        //调用框架
        try {
            if (advisor.intercept(cls, method, params)) {
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
        advisor.begin();
    }

    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        advisor.before(cls, method, params);
    }

    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
        advisor.after(cls, method, params);
    }

    public void error(Class<?> cls, Method method, Object[] params, Throwable e) {
        advisor.error(cls, method, params,e);
    }

    public void end() {
        advisor.end();
    }
}