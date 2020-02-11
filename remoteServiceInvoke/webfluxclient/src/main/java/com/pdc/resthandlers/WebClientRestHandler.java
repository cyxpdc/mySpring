package com.pdc.resthandlers;

import com.pdc.beans.MethodInfo;
import com.pdc.beans.ServerInfo;
import com.pdc.interfaces.RestHandler;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import reactor.core.publisher.Mono;

/**
 * @author pdc
 */
public class WebClientRestHandler implements RestHandler {

	private WebClient webClient;
	private RequestBodySpec request;

	/**
	 * 初始化webclient
	 */
	@Override
	public void init(ServerInfo serverInfo) {
		this.webClient = WebClient.create(serverInfo.getUrl());
	}

	/**
	 * 处理rest请求
	 */
	@Override
	public Object invokeRest(MethodInfo methodInfo) {
		// 返回结果
		Object result = null;
		//基本请求
		request = webClient
				// 请求方法
				.method(methodInfo.getMethod())
				// 请求url 和 参数
				.uri(methodInfo.getUrl(), methodInfo.getParams())
				.accept(MediaType.APPLICATION_JSON);
		//使请求可以增加判断，恢复为接口的方法
		ResponseSpec retrieve = null;
		// 判断是否带了body,如RequestBody
		if (methodInfo.getBody() != null) {
			// 发出请求
			retrieve = request
					.body(methodInfo.getBody(), methodInfo.getBodyElementType())
					.retrieve();
		} else {
			retrieve = request.retrieve();
		}
		// 处理异常
		retrieve.onStatus(status -> status.value() == 404,
				response -> Mono.just(new RuntimeException("Not Found")));
		// 处理返回值
		if (methodInfo.isReturnFlux()) {
			result = retrieve.bodyToFlux(methodInfo.getReturnElementType());
		} else {
			result = retrieve.bodyToMono(methodInfo.getReturnElementType());
		}
		return result;
	}
}
