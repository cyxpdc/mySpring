package org.smart4j.plugin.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 判断当前用户是否已登录（包括：已认证 与 已记住）
 *
 * @author pdc
 */
@Target({ElementType.TYPE, ElementType.METHOD})//类、方法
@Retention(RetentionPolicy.RUNTIME)//运行期
public @interface User {
}
