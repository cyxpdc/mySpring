package com.pdc.plugin.rest;

import java.util.Arrays;
import java.util.List;
import com.pdc.spring.helper.ConfigHelper;
import com.pdc.spring.util.StringUtil;

/**
 * 从配置文件中获取相关属性
 *
 * @author pdc
 * @since 1.0.0
 */
public class RestConfig {

    public static boolean isLog() {
        return ConfigHelper.getBoolean(RestConstant.LOG);
    }
    
    public static boolean isJsonp() {
        return ConfigHelper.getBoolean(RestConstant.JSONP);
    }

    public static String getJsonpFunction() {
        return ConfigHelper.getString(RestConstant.JSONP_FUNCTION);
    }

    public static boolean isCors() {
        return ConfigHelper.getBoolean(RestConstant.CORS);
    }

    public static List<String> getCorsOriginList() {
        String corsOrigin = ConfigHelper.getString(RestConstant.CORS_ORIGIN);
        return Arrays.asList(StringUtil.splitString(corsOrigin, ","));
    }
}
