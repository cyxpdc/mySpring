package com.pdc.spring.proxy;

/**
 * 代理接口
 *
 * @author pdc
 */
public interface Proxy {

    /**
     * 执行链式代理
     */
    Object doProxy(ProxyChain proxyChain) throws Throwable;
}