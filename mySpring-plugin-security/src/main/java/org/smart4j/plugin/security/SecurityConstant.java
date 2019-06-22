package org.smart4j.plugin.security;

/**
 * 常量接口
 *
 * @author huangyong
 * @since 1.0.0
 */
public interface SecurityConstant {

    String REALMS = "smart.plugin.security.realms";
    String REALMS_JDBC = "jdbc";
    String REALMS_CUSTOM = "custom";

    String SMART_SECURITY = "smart.plugin.security.custom.class";

    String JDBC_AUTHC_QUERY = "smart.plugin.security.jdbc.authc_query";
    String JDBC_ROLES_QUERY = "smart.plugin.security.jdbc.roles_query";
    String JDBC_PERMISSIONS_QUERY = "smart.plugin.security.jdbc.permissions_query";

    String CACHE = "smart.plugin.security.cache";
}
