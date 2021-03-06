package com.pdc.test4.aspect;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pdc.spring.annotation.Aspect;
import com.pdc.spring.annotation.Controller;
import com.pdc.spring.proxy.BaseAspectProxy;

/**
 * 拦截 Controller 所有方法
 * @author pdc
 */
@Aspect(Controller.class)
public class ControllerBaseAspect extends BaseAspectProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerBaseAspect.class);

    private long begin;

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        LOGGER.debug("---------- begin ----------");
        LOGGER.debug(String.format("class: %s", cls.getName()));
        LOGGER.debug(String.format("method: %s", method.getName()));
        begin = System.currentTimeMillis();
    }

    @Override
    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
        LOGGER.debug(String.format("time: %dms", System.currentTimeMillis() - begin));
        LOGGER.debug("----------- end -----------");
    }
}
