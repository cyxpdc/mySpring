package com.pdc.plugin.soap;

import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import com.pdc.spring.helper.BeanHelper;
import com.pdc.spring.helper.ClassHelper;
import com.pdc.spring.util.CollectionUtil;
import com.pdc.spring.util.StringUtil;

/**
 * SOAP Servlet
 * 拦截所有的SOAP请求，该请求路径的前缀就是SoapConstant.SERVLET_URL = "/soap/*";
 *
 * @author pdc
 * @since 1.0.0
 */
@WebServlet(urlPatterns = SoapConstant.SERVLET_URL, loadOnStartup = 0)
public class SoapServlet extends CXFNonSpringServlet {
    /**
     * 初始化 CXF 总线，发布 SOAP 服务
     * @WebServlet中设置了loadOnstartup=0，表示该web容器启动时会自动加载Servlet
     * 即调用CXFNonSpringServlet#init，init会调用此方法
     * @param sc
     */
    @Override
    protected void loadBus(ServletConfig sc) {
        // 初始化 CXF 总线
        super.loadBus(sc);
        Bus bus = getBus();
        BusFactory.setDefaultBus(bus);
        // 发布 SOAP 服务
        publishSoapService();
    }

    private void publishSoapService() {
        // 遍历所有标注了 SOAP 注解的类
        Set<Class<?>> soapClassSet = ClassHelper.getClassSetByAnnotation(Soap.class);
        if (CollectionUtil.isNotEmpty(soapClassSet)) {
            for (Class<?> soapClass : soapClassSet) {
                // 获取 SOAP 地址
                String address = getAddress(soapClass);
                // 获取 SOAP 类的接口
                Class<?> soapInterfaceClass = getSoapInterfaceClass(soapClass);
                // 获取 SOAP 类的实例,@Service将其注入到mySpring中了
                Object soapInstance = BeanHelper.getBean(soapClass);
                // 发布 SOAP 服务
                SoapHelper.publishService(address, soapInterfaceClass, soapInstance);
            }
        }
    }

    /**
     * 获取 SOAP 类的接口
     * @param soapClass
     * @return
     */
    private Class<?> getSoapInterfaceClass(Class<?> soapClass) {
        // 获取 SOAP 实现类的第一个接口作为 SOAP 服务接口
        return soapClass.getInterfaces()[0];
    }

    /**
     * 获取 SOAP 地址
     * 因为webservice注解中已经定义了前缀，这里直接获取接口名即可，前缀会自动补上
     * @param soapClass
     * @return
     */
    private String getAddress(Class<?> soapClass) {
        String address;
        // 若 SOAP 注解的 value 属性不为空，则获取当前值，否则获取接口名
        String soapValue = soapClass.getAnnotation(Soap.class).value();
        if (StringUtil.isNotEmpty(soapValue)) {
            address = soapValue;
        } else {
            address = getSoapInterfaceClass(soapClass).getSimpleName();
        }
        // 确保最前面只有一个 /
        if (!address.startsWith("/")) {
            address = "/" + address;
        }
        address = address.replaceAll("\\/+", "/");
        return address;
    }
}
