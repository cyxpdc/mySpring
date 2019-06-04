package com.imooc.handlers;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import com.imooc.exceptions.CheckException;

import reactor.core.publisher.Mono;

@Component
@Order(-2)//注：优先级调高，数字越小，优先级越高
public class ExceptionHandler implements WebExceptionHandler {

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpResponse response = exchange.getResponse();
		// 设置响应头400,即错误的请求
		response.setStatusCode(HttpStatus.BAD_REQUEST);
		// 设置返回类型，文本，也可以json对象
		response.getHeaders().setContentType(MediaType.TEXT_PLAIN);

		// 异常信息
		String errorMsg = toStr(ex);

		DataBuffer db = response.bufferFactory().wrap(errorMsg.getBytes());
		//参数为publish
		return response.writeWith(Mono.just(db));
	}

	private String toStr(Throwable ex) {
		// 已知异常
		if (ex instanceof CheckException) {
			CheckException e = (CheckException) ex;
			return e.getFieldName() + ": invalid value " + e.getFieldValue();
		}
		// 未知异常, 需要打印堆栈, 方便定位
		else {
			ex.printStackTrace();
			return ex.toString();
		}

	}

}
