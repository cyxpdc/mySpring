package com.pdc.routers;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.pdc.handlers.UserHandler;

@Configuration
public class AllRouters {

	@Bean
	RouterFunction<ServerResponse> userRouter(UserHandler handler) {
		return RouterFunctions.nest(
				// 相当于类上面的 @RequestMapping("/user")
				//满足条件就会进来
				path("/user"),
				// 下面的相当于类里面的 @xxx(Request)Mapping
				// 得到所有用户(GetMapping)
				RouterFunctions.route(GET("/"), handler::getAllUser)
				// 创建用户
				.andRoute(POST("/").and(accept(MediaType.APPLICATION_JSON_UTF8)),
								handler::createUser)
				// 删除用户
				.andRoute(DELETE("/{id}"), handler::deleteUserById));
	}

}
