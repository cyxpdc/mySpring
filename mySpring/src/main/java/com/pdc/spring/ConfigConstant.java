package com.pdc.spring;

/**
 * 提供相关配置项常量
 * @author pdc
 */
public interface ConfigConstant {

    String CONFIG_FILE = "smart.properties";

    String JDBC_DRIVER = "smart.framework.jdbc.driver";
    String JDBC_URL = "smart.framework.jdbc.url";
    String JDBC_USERNAME = "smart.framework.jdbc.username";
    String JDBC_PASSWORD = "smart.framework.jdbc.password";
    /**
     * 包名
     */
    String APP_BASE_PACKAGE = "smart.framework.app.base_package";
    /**
     * jsp路径
     */
    String APP_JSP_PATH = "smart.framework.app.jsp_path";
    /**
     * 静态资源路径
     */
    String APP_ASSET_PATH = "smart.framework.app.asset_path";
    /**
     * 上传文件大小限制
     */
    String APP_UPLOAD_LIMIT = "smart.framework.app.upload_limit";
}
