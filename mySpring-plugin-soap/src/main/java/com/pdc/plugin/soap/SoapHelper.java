package com.pdc.plugin.soap;

import java.util.ArrayList;
import java.util.List;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.message.Message;

/**
 * SOAP 助手类
 * @author pdc
 */
public class SoapHelper {

    private static final List<Interceptor<? extends Message>> inInterceptorList =
                                                                        new ArrayList<>();
    private static final List<Interceptor<? extends Message>> outInterceptorList =
                                                                        new ArrayList<>();

    /**
     * 初始化CXF提供的日志拦截器
     */
    static {
        if (SoapConfig.isLog()) {
            inInterceptorList.add(new LoggingInInterceptor());
            outInterceptorList.add(new LoggingOutInterceptor());
        }
    }

    /**
     * 使用ServerFactoryBean发布SOAP服务
     * 由SoapServlet调用
     * @param wsdl wsdl路径
     * @param interfaceClass 接口类
     * @param implementInstance 实现类
     */
    public static void publishService(String wsdl, Class<?> interfaceClass, Object implementInstance) {
        ServerFactoryBean factory = new ServerFactoryBean();
        factory.setAddress(wsdl);
        factory.setServiceClass(interfaceClass);
        factory.setServiceBean(implementInstance);
        factory.setInInterceptors(inInterceptorList);
        factory.setOutInterceptors(outInterceptorList);
        factory.create();
    }

    /**
     * 使用ClientProxyFactoryBean创建SOAP客户端
     * @param wsdl wsdl路径
     * @param interfaceClass 接口类
     * @param <T>
     * @return
     */
    public static <T> T createClient(String wsdl, Class<? extends T> interfaceClass) {
        ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
        factory.setAddress(wsdl);
        factory.setServiceClass(interfaceClass);
        factory.setInInterceptors(inInterceptorList);
        factory.setOutInterceptors(outInterceptorList);
        return factory.create(interfaceClass);
    }
}
