package com.pdc.plugin.rest;

import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import com.pdc.spring.helper.ClassHelper;
import com.pdc.spring.util.CollectionUtil;
import com.pdc.spring.util.StringUtil;

/**
 * REST Servlet
 *
 * @author pdc
 * @since 1.0.0
 */
@WebServlet(urlPatterns = RestConstant.SERVLET_URL, loadOnStartup = 0)
public class RestServlet extends CXFNonSpringServlet {

    @Override
    protected void loadBus(ServletConfig sc) {
        // 初始化 CXF 总线
        super.loadBus(sc);
        Bus bus = getBus();
        BusFactory.setDefaultBus(bus);
        // 发布 REST 服务
        publishRestService();
    }

    private void publishRestService() {
        // 遍历所有标注了 REST 注解的类
        Set<Class<?>> restClassSet = ClassHelper.getClassSetByAnnotation(Rest.class);
        if (CollectionUtil.isNotEmpty(restClassSet)) {
            for (Class<?> restClass : restClassSet) {
                // 获取 REST 地址
                String address = getAddress(restClass);
                // 发布 REST 服务
                RestHelper.publishService(address, restClass);
            }
        }
    }

    private String getAddress(Class<?> restClass) {
        String address;
        // 若 REST 注解的 value 属性不为空，则获取当前值，否则获取类名
        String value = restClass.getAnnotation(Rest.class).value();
        if (StringUtil.isNotEmpty(value)) {
            address = value;
        } else {
            address = restClass.getSimpleName();
        }
        // 确保最前面只有一个 /
        if (!address.startsWith("/")) {
            address = "/" + address;
        }
        address = address.replaceAll("\\/+", "/");
        return address;
    }
}
