package com.pdc.spring.helper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.pdc.spring.annotation.Action;
import com.pdc.spring.bean.Handler;
import com.pdc.spring.bean.Request;
import com.pdc.spring.util.ArrayUtil;
import com.pdc.spring.util.CollectionUtil;

/**
 * 控制器助手类
 * 根据指定的requestMethod, requestPath封装Request就能得到对应Handler的controllerClass, method
 * @author pdc
 */
public final class ControllerHelper {
    /**
     * 用于存放请求与处理器的映射关系:
     * requestMethod, requestPath:controllerClass, method
     */
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<>();

    static {
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();//获取所有Controller类
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            //遍历所有Controller类
            for (Class<?> controllerClass : controllerClassSet) {
                Method[] methods = controllerClass.getDeclaredMethods();//获取当前controller类的所有方法
                if (ArrayUtil.isNotEmpty(methods)) {
                    for (Method method : methods) {//遍历所有方法
                        if (method.isAnnotationPresent(Action.class)) {//若当前方法有Action注解
                            //从Action注解中获取URL映射规则
                            Action action = method.getAnnotation(Action.class);
                            String mapping = action.value();//获取请求类型与路径
                            //匹配，如：@Action("get:/customer")
                            //+:一次或多次匹配前面的字符或子表达式
                            //*:零次或多次匹配前面的字符或子表达式
                            if (mapping.matches("\\w+:/\\w*")) {
                                String[] array = mapping.split(":");
                                //封装数据
                                if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                                    //array[0]是requestMethod，array[1]是requestPath
                                    Request request = new Request(array[0], array[1]);
                                    Handler handler = new Handler(controllerClass, method);
                                    ACTION_MAP.put(request, handler);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取 Handler
     */
    public static Handler getHandler(String requestMethod, String requestPath) {
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }
}
