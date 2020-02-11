package com.pdc.spring.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.pdc.spring.bean.FactoryBean;
import com.pdc.spring.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean容器
 * @author pdc
 */
public final class BeanHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanHelper.class);
    /**
     * 保存所有bean实例
     * 此处线程安全：https://www.oschina.net/question/2842581_2262826
     * 对象都是单例的
     */
    private static final Map<Class<?>, Object> BEAN_MAP = new HashMap<>();

    static {//获取所有Bean实例
        Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
        for (Class<?> beanClass : beanClassSet) {
            Object obj = null;
            if(beanClass.isAssignableFrom(FactoryBean.class)){
                try {
                    obj = ReflectionUtil.invokeMethod(ReflectionUtil.newInstance(beanClass),beanClass.getMethod("getObject"));
                } catch (NoSuchMethodException e) {
                    LOGGER.error("获取factorybean失败");
                }
            }else{
                obj = ReflectionUtil.newInstance(beanClass);
            }
            BEAN_MAP.put(beanClass, obj);
        }

    }

    /**
     * 获取 Bean 映射
     */
    public static Map<Class<?>, Object> getBeanMap() {
        return BEAN_MAP;
    }

    /**
     * 获取 Bean 实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<?> cls) {
        if (!BEAN_MAP.containsKey(cls)) {
            throw new RuntimeException("can not get bean by class: " + cls);
        }
        return (T) BEAN_MAP.get(cls);
    }

    /**
     * 设置 Bean 实例
     */
    public static void setBean(Class<?> cls, Object obj) {
        BEAN_MAP.put(cls, obj);
    }
}