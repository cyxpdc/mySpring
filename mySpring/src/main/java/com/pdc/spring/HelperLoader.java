package com.pdc.spring;

import com.pdc.spring.util.ClassUtil;
import com.pdc.spring.helper.AopHelper;
import com.pdc.spring.helper.BeanHelper;
import com.pdc.spring.helper.ClassHelper;
import com.pdc.spring.helper.ControllerHelper;
import com.pdc.spring.helper.IocHelper;

/**
 * 加载相应的 Helper 类
 *
 * @author pdc
 */
public final class HelperLoader {

    public static void init() {
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                AopHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName());//加载类
        }
    }
}