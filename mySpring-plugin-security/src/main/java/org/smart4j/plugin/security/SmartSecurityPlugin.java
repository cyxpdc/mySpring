package org.smart4j.plugin.security;

import java.util.Set;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.shiro.web.env.EnvironmentLoaderListener;

/**
 * Smart Security 插件
 * 用来注册Shiro
 *
 * @author pdc
 */
public class SmartSecurityPlugin implements ServletContainerInitializer {

    public void onStartup(Set<Class<?>> handlesTypes, ServletContext servletContext) throws ServletException {
        // 设置初始化参数，定制默认的应用程序配置文件
        servletContext.setInitParameter("shiroConfigLocations", "classpath:smart-security.ini");
        // 注册 Listener(EnvironmentLoaderListener)
        servletContext.addListener(EnvironmentLoaderListener.class);
        // 注册 Filter(自定义SmartSecurityFilter，扩展了ShiroFilter)
        FilterRegistration.Dynamic smartSecurityFilter = servletContext.addFilter("SmartSecurityFilter", SmartSecurityFilter.class);
        smartSecurityFilter.addMappingForUrlPatterns(null, false, "/*");
    }
}
