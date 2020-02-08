package com.pdc.spring.helper;

import java.util.Properties;
import com.pdc.spring.ConfigConstant;
import com.pdc.spring.util.PropsUtil;

/**
 * 属性文件助手类
 * 用来获取配置文件的相关属性
 * String有多个方法，值得抽取，int只有一个，就没必要了
 * @author pdc
 */
public final class ConfigHelper {
    /**
     * 保存smart.properties文件的属性
     */
    private static final Properties CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);

    /**
     * 获取 JDBC 驱动
     */
    public static String getJdbcDriver() {
        return getString(ConfigConstant.JDBC_DRIVER);
        //return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_DRIVER);
    }

    /**
     * 获取 JDBC URL
     */
    public static String getJdbcUrl() {
        return getString(ConfigConstant.JDBC_URL);
        //return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_URL);
    }

    /**
     * 获取 JDBC 用户名
     */
    public static String getJdbcUsername() {
        return getString(ConfigConstant.JDBC_USERNAME);
        //return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_USERNAME);
    }

    /**
     * 获取 JDBC 密码
     */
    public static String getJdbcPassword() {
        return getString(ConfigConstant.JDBC_PASSWORD);
        //return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_PASSWORD);
    }

    /**
     * 获取应用基础包名
     */
    public static String getAppBasePackage() {
        return getString(ConfigConstant.APP_BASE_PACKAGE);
        //return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_BASE_PACKAGE);
    }

    /**
     * 获取应用 JSP 路径
     */
    public static String getAppJspPath() {
        return getString(ConfigConstant.APP_JSP_PATH,"/WEB-INF/view/");
        //return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_JSP_PATH, "/WEB-INF/view/");
    }

    /**
     * 获取应用静态资源路径
     */
    public static String getAppAssetPath() {
        return getString(ConfigConstant.APP_ASSET_PATH,"/asset/");
        //return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_ASSET_PATH, "/asset/");
    }

    /**
     * 根据属性名获取 String 类型的属性值，带默认值
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(String key,String defaultValue) {
        return PropsUtil.getString(CONFIG_PROPS, key, defaultValue);
    }

    /**
     * 根据属性名获取 String 类型的属性值
     */
    public static String getString(String key) {
        return PropsUtil.getString(CONFIG_PROPS, key);
    }

    /**
     * 获取应用文件上传限制
     */
    public static int getAppUploadLimit() {
        //return getInt(ConfigConstant.APP_UPLOAD_LIMIT,10);
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.APP_UPLOAD_LIMIT, 10);
    }
    /**
     * 根据属性名获取 boolean 类型的属性值
     */
    public static boolean getBoolean(String key) {
        return PropsUtil.getBoolean(CONFIG_PROPS, key);
    }
}
