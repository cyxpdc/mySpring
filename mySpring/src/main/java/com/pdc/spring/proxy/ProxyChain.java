package com.pdc.spring.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 代理链
 * 可能每种类有多个代理，所以用它，为额外补充
 * Controller调用service的方法时，调用此类的doProxyChain
 * @author pdc
 */
public class ProxyChain {

    private final Class<?> targetClass;//目标类
    private final Object targetObject;//目标对象，加上targetMethod可用来完成Spring的通知功能
    private final Method targetMethod;//目标方法
    private final MethodProxy methodProxy;//方法代理
    private final Object[] methodParams;//方法参数

    private List<Proxy> proxyList;//代理列表
    private int proxyIndex = 0;//代理索引

    public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod, MethodProxy methodProxy, Object[] methodParams, List<Proxy> proxyList) {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.methodProxy = methodProxy;
        this.methodParams = methodParams;
        this.proxyList = proxyList;
    }
    /**
     * proxyIndex充当代理对象的计数器
     * 若尚未达到proxyList的上限，则从proxyList中去取出相应的Proxy对象，并调用其doProxy()
     * Proxy接口的实现中会提供相应的横切逻辑，并调用doProxyChain()
     * 随后将再次调用当前ProxyChain对象的doProxyChain()，直到proxyIndex达到proxyList的上限为止
     * 最后调用methodProxy的invokeSuper方法，执行目标对象的业务逻辑
     * @return
     * @throws Throwable
     */
    public Object doProxyChain() throws Throwable {
        Object methodResult;
        if (proxyIndex < proxyList.size()) {
            methodResult = proxyList.get(proxyIndex++).doProxy(this);
        } else {
            methodResult = methodProxy.invokeSuper(targetObject, methodParams);
        }
        return methodResult;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }
}