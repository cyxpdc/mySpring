package com.pdc.spring.helper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pdc.spring.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pdc.spring.annotation.Service;
import com.pdc.spring.proxy.AspectProxy;
import com.pdc.spring.proxy.Proxy;
import com.pdc.spring.proxy.ProxyManager;
import com.pdc.spring.proxy.TransactionProxy;

/**
 * 方法拦截助手类
 * @author pdc
 */
public final class AopHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

    static {
        try {
            //获取代理类：目标类集合的map
            //因为通过目标类无法得知代理类，只有通过代理类才能知道目标类，所以需要proxyMap
            Map<Class<?>, Set<Class<?>>> proxyMap = createProxyclassTargetMap();
            //通过proxyMap获取目标类：代理对象列表的map
            Map<Class<?>, List<Proxy>> targetMap = createTargetProxylistMap(proxyMap);
            //遍历targetMap，将该代理对象放入BeanMap中
            for (Map.Entry<Class<?>, List<Proxy>> targetEntry : targetMap.entrySet()) {
                Class<?> targetClass = targetEntry.getKey();//如@Controller
                List<Proxy> proxyList = targetEntry.getValue();
                //动态代理，将targetClass交给CGlib，proxyList是额外的
                Object proxy = ProxyManager.createProxy(targetClass, proxyList);
                BeanHelper.setBean(targetClass, proxy);
            }
        } catch (Exception e) {
            LOGGER.error("aop failure", e);
        }
    }

    /**
     * 代理类：目标类
     * @return
     * @throws Exception
     */
    private static Map<Class<?>, Set<Class<?>>> createProxyclassTargetMap() throws Exception {
        Map<Class<?>, Set<Class<?>>> proxyMap = new HashMap<>();
        addAspectProxy(proxyMap);
        addTransactionProxy(proxyMap);//TransactionProxy：Service
        return proxyMap;
    }

    /**
     * 获取代理类与目标类的映射关系
     * 代理类需要扩展AspectProxy，并带有@Aspect
     * 如@Aspect(Controller.class)的代理类对应所有带有@Controller的类
     * 可能有多个@Aspect(Controller.class)代理类，即有不同的proxyClass对应相同的targetClassSet
     * @param proxyMap
     * @throws Exception
     */
    private static void addAspectProxy(Map<Class<?>, Set<Class<?>>> proxyMap){
        //遍历扩展AspectProxy的类，即切面类
        for (Class<?> proxyClass : ClassHelper.getClassSetBySuper(AspectProxy.class)) {
            if (proxyClass.isAnnotationPresent(Aspect.class)) {//如果带有@Aspect
                Aspect aspect = proxyClass.getAnnotation(Aspect.class);
                Set<Class<?>> targetClassSet = createTargetClassSet(aspect);//得到该切面对应的注解类集合
                proxyMap.put(proxyClass, targetClassSet);
            }
        }
    }

    private static void addTransactionProxy(Map<Class<?>, Set<Class<?>>> proxyMap) {
        Set<Class<?>> serviceClassSet = ClassHelper.getClassSetByAnnotation(Service.class);
        proxyMap.put(TransactionProxy.class, serviceClassSet);
    }

    /**
     * 获取Aspect注解中设置的注解类
     * 若该注解类不是Aspect类，则调用ClassHelper.getClassSetByAnnotation(annotation)获取相关类
     * 并把这些类放入目标类集合中，返回这个集合
     * @param aspect
     * @return
     * @throws Exception
     */
    private static Set<Class<?>> createTargetClassSet(Aspect aspect){
        Set<Class<?>> targetClassSet = new HashSet<>();
        Class<? extends Annotation> annotation = aspect.value();//该注解对应的类，如Controller
        if (annotation != null && !annotation.equals(Aspect.class)) {
            targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(annotation));
        }
        return targetClassSet;
    }

    /**
     * 根据代理类和目标类集合之间的映射关系分析出目标类与代理对象列表之间的映射关系
     * 如@Aspect(Controller.class)的代理类有多个，则对应所有带有@Controller的类有多个代理类
     * @param proxyMap
     * @return
     * @throws Exception
     */
    private static Map<Class<?>, List<Proxy>> createTargetProxylistMap(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        Map<Class<?>, List<Proxy>> targetMap = new HashMap<>();
        for (Map.Entry<Class<?>, Set<Class<?>>> proxyEntry : proxyMap.entrySet()) {
            //代理类,如test5的ControllerAspect，和security工程的AuthzAnnotationAspect
            Class<?> proxyClass = proxyEntry.getKey();
            Set<Class<?>> targetClassSet = proxyEntry.getValue();//目标类集合，如所有Controller
            for (Class<?> targetClass : targetClassSet) {
                Proxy proxy = (Proxy) proxyClass.newInstance();
                if (targetMap.containsKey(targetClass)) {//如果包含这个目标类，则添加代理类
                    targetMap.get(targetClass).add(proxy);
                } else {//如果不包含这个目标类，则创建，再添加代理类
                    List<Proxy> proxyList = new ArrayList<>();
                    proxyList.add(proxy);
                    targetMap.put(targetClass, proxyList);
                }
            }
        }
        return targetMap;
    }
}