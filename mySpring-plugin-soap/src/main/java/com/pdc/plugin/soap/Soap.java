package com.pdc.plugin.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SOAP 服务注解
 * 这样，如果某实现类实现了多个接口，就不用在SoapServelt里面写死为第一个接口
 * @author pdc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Soap {

    /**
     * 服务名
     * 若为空，则默认使用服务类名为SOAP的服务，如CustomerSoapService
     */
    String value() default "";
}
