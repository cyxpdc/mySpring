package com.pdc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务器相关信息
 * 
 * @author pdc
 *
 */
@Target(ElementType.TYPE) //加在类上
@Retention(RetentionPolicy. RUNTIME) //运行期
public @interface ApiServer {
	/**
	 * 方法
	 * @return
	 */
	String value() default "";
}
