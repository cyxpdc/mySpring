package com.pdc.beans;

import java.util.Map;

import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 方法调用信息类
 * 
 * @author pdc
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodInfo {

	/**
	 * 请求url
	 */
	private String url;

	/**
	 * 请求方法
	 * 枚举类：GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS,TRACE;
	 */
	private HttpMethod method;

	/**
	 * 请求参数(url上的)
	 * 这里需要注意，因为可能有多个参数，如url上的{id}为key，方法的入参String id为value
	 */
	private Map<String, Object> params;

	/**
	 * 请求body，如@RequestBody
	 */
	private Mono body;
	
	/**
	 * 请求body的类型,如@RequestBody对应的参数的返回值泛型
	 */
	private Class<?> bodyElementType;
	
	/**
	 * 返回值，是flux还是mono
	 */
	private boolean returnFlux;
	
	/**
	 * 返回对象的类型，即<>里面的泛型
	 */
	private Class<?> returnElementType;

}
