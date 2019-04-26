package com.pdc.spring.helper;

import java.lang.reflect.Field;
import java.util.Map;

import com.pdc.spring.annotation.Inject;
import com.pdc.spring.util.ArrayUtil;
import com.pdc.spring.util.CollectionUtil;
import com.pdc.spring.util.ReflectionUtil;

/**
 * 依赖注入助手类
 * 初始化@Inject注解的变量
 * 遍历所有类，判断其成员变量是否有@Inject，若有，判断是否有这个注解类的实例(BeanMap)
 * 若有，newInstance即可
 *
 * @author pdc
 */
public final class IocHelper {
    //对象都是单例的，因为beanMap是BeanHelper事先创建好，在这里直接获取的
    static {
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();//获取所有Bean
        if (CollectionUtil.isNotEmpty(beanMap)) {
            //遍历所有Bean
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                Class<?> beanClass = beanEntry.getKey();//类名
                Object beanInstance = beanEntry.getValue();//对应的实例
                Field[] beanFields = beanClass.getDeclaredFields();//属性
                if (ArrayUtil.isNotEmpty(beanFields)) {
                    //遍历当前Bean的所有属性
                    for (Field beanField : beanFields) {
                        //如果beanField带有Inject注解(如Controller中的Service层)
                        if (beanField.isAnnotationPresent(Inject.class)) {
                            //通过BeanMap和Inject对应的类名获取属性对应的实例(即Service实例)
                            Class<?> beanFieldClass = beanField.getType();
                            Object beanFieldInstance = beanMap.get(beanFieldClass);
                            if (beanFieldInstance != null) {//如果存在这个属性对应的实例
                                //初始化属性值(即new Service):beanField.set(beanInstance,beanFieldInstance)
                                //将beanInstance(Controller)的beanField(Service)设置为beanFieldInstance(实现类)
                                //注意，在这里我们直接用的实现类，没有加接口，所以可以直接这么写
                                //是最简单的写法
                                ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
