package com.pdc.spring.util;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 数组工具类
 * @author pdc
 */
public final class ArrayUtil {

    /**
     * 判断数组是否非空
     */
    public static boolean isNotEmpty(Object[] array) {
        return !ArrayUtils.isEmpty(array);
    }
}
