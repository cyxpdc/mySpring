package com.pdc.plugin.soap;

import com.pdc.spring.helper.ConfigHelper;

/**
 * 从配置文件smart.properties中获取相关属性
 *
 * @author pdc
 * @since 1.0.0
 */
public class SoapConfig {

    public static boolean isLog() {
        return ConfigHelper.getBoolean(SoapConstant.LOG);
    }
}
