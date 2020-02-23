package com.pdc.spring.proxy;

import java.lang.reflect.Method;
import java.util.List;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 代理管理器
 * 可以理解为CGlib中的代理对象，不过因为额外补充，所以代理对象任务交给了ProxyChain
 * @author pdc
 */
public class ProxyManager {
    /**
     * 创建代理对象
     * @param targetClass
     * @param proxyList
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(final Class<?> targetClass, final List<Proxy> proxyList) {
        return (T) Enhancer.create(targetClass, (MethodInterceptor) (targetObject, targetMethod, methodParams, methodProxy) -> new ProxyChain
                (targetClass, targetObject, targetMethod, methodProxy, methodParams, proxyList)
                .doProxyChain());
    }

}