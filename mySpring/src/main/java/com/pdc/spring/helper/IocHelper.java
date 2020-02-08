package com.pdc.spring.helper;

import java.lang.reflect.Field;
import java.util.Map;

import com.pdc.spring.annotation.Inject;
import com.pdc.spring.annotation.Inject2Name;
import com.pdc.spring.util.ArrayUtil;
import com.pdc.spring.util.ClassUtil;
import com.pdc.spring.util.CollectionUtil;
import com.pdc.spring.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 依赖注入助手类
 * 初始化@Inject注解的变量
 * 遍历所有类，判断其成员变量是否有@Inject，
 * 若有，判断是否有这个注解类的实例(BeanMap)，
 * 若有，newInstance即可
 * @author pdc
 */
public final class IocHelper {

    private static Logger LOGGER = LoggerFactory.getLogger(IocHelper.class);

    static {
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();//获取所有Bean
        if (CollectionUtil.isNotEmpty(beanMap)) {
            //遍历所有Bean
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                Class<?> beanClass = beanEntry.getKey();//类名
                Object beanInstance = beanEntry.getValue();//当前类名对应的实例
                Field[] beanFields = beanClass.getDeclaredFields();//其所有属性
                if (ArrayUtil.isNotEmpty(beanFields)) {
                    //遍历当前Bean的所有属性
                    for (Field beanField : beanFields) {
                        //如果beanField带有Inject注解(如Controller中的Service层)
                        if (beanField.isAnnotationPresent(Inject.class)) {
                            //通过BeanMap和Inject对应的类名获取属性对应的实例(即Service实例)
                            Class<?> beanFieldClass = beanField.getType();
                            setField(beanMap, beanInstance, beanField, beanFieldClass);
                        }else if(beanField.isAnnotationPresent(Inject2Name.class)){
                            String className = beanField.getAnnotation(Inject2Name.class).name();
                            setField(beanMap, beanInstance, beanField, ClassUtil.loadClass(className));
                        }
                    }
                }
            }
        }
    }

    private static void setField(Map<Class<?>, Object> beanMap, Object beanInstance, Field beanField, Class<?> beanFieldClass) {
        Object beanFieldInstance = beanMap.get(beanFieldClass);
        if (beanFieldInstance != null) {
            ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
            return ;
        }
        LOGGER.warn("找不到类"+ beanFieldClass + "的实例");//也可以修改为异常
    }
}
