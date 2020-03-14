package com.pdc.spring.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.pdc.spring.annotation.Controller;
import com.pdc.spring.annotation.Service;
import com.pdc.spring.bean.FactoryBean;
import com.pdc.spring.util.ClassUtil;
import com.pdc.spring.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类操作助手类，封装ClassUtil
 * @author pdc
 */
public final class ClassHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassHelper.class);

    /**
     * 定义类集合（用于存放所加载的类）
     */
    private static final Set<Class<?>> CLASS_SET;

    /**
     * 获取应用程序类集合
     */
    static {
        CLASS_SET = ClassUtil.getClassSet(ConfigHelper.getAppBasePackage());
    }

    /**
     * 获取应用包名下所有 Bean 类（包括：Service、Controller 等）
     * 添加：FactoryBean https://blog.csdn.net/zknxx/article/details/79572387
     */
    public static Set<Class<?>> getBeanClassSet() {
        Set<Class<?>> beanClassSet = new HashSet<>();
        beanClassSet.addAll(getServiceClassSet());
        beanClassSet.addAll(getControllerClassSet());
        beanClassSet.addAll(getFactoryBeanClassSet());
        return beanClassSet;
    }

    /**
     * 获取应用包名下所有 Service 类
     */
    public static Set<Class<?>> getServiceClassSet() {
        return getClassSetByAnnotation(Service.class);
    }

    /**
     * 获取应用包名下所有 Controller 类
     */
    public static Set<Class<?>> getControllerClassSet() {
        return getClassSetByAnnotation(Controller.class);
    }

    /**
     * 获取应用包名下带有某注解的所有类
     * 因为需要获取带有Aspect注解的所有类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass) {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls : CLASS_SET) {
            if (cls.isAnnotationPresent(annotationClass)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * https://blog.csdn.net/zknxx/article/details/79572387
     * @return
     */
    private static Set<Class<?>> getFactoryBeanClassSet() {
        Set<Class<?>> factoryBeanClassSet = new HashSet<>();
        for(Class<?> cls : getClassSetBySuper(FactoryBean.class)){
            factoryBeanClassSet.add(cls);
        }
        return factoryBeanClassSet;
    }

    /**
     * 获取应用包名下某父类（或接口）的所有子类（或实现类）
     * 因为需要扩展AspectProxy抽象类的所有具体类
     */
    public static Set<Class<?>> getClassSetBySuper(Class<?> superClass) {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls : CLASS_SET) {
            if (superClass.isAssignableFrom(cls) && !superClass.equals(cls)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }
}
