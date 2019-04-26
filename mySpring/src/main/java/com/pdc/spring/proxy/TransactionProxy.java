package com.pdc.spring.proxy;

import com.pdc.spring.annotation.Transaction;
import com.pdc.spring.helper.DatabaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事务代理
 *
 * @author pdc
 */
public class TransactionProxy implements Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProxy.class);
    /**
     * 保证同一线程中事务控制相关逻辑只会执行一次
     */
    private static final ThreadLocal<Boolean> FLAG_HOLDER = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result;
        boolean flag = FLAG_HOLDER.get();
        //如果是第一次执行，且该方法有Transaction注解
        if (!flag && proxyChain.getTargetMethod().isAnnotationPresent(Transaction.class)) {
            FLAG_HOLDER.set(true);
            try {
                DatabaseHelper.beginTransaction();
                LOGGER.debug("begin transaction");
                result = proxyChain.doProxyChain();
                DatabaseHelper.commitTransaction();
                LOGGER.debug("commit transaction");
            } catch (Exception e) {
                DatabaseHelper.rollbackTransaction();
                LOGGER.debug("rollback transaction");
                throw e;
            } finally {
                FLAG_HOLDER.remove();
            }
        } else {
            result = proxyChain.doProxyChain();
        }
        return result;
    }
}