package com.pdc.handlers;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.pdc.domain.User;
import com.pdc.repository.UserRepository;
import com.pdc.util.CheckUtil;

import reactor.core.publisher.Mono;

@Component
public class UserHandler {

	private final UserRepository repository;

	public UserHandler(UserRepository rep) {
		this.repository = rep;
	}

	/**
	 * 得到所有用户
	 * 
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> getAllUser(ServerRequest request) {
		return ok().contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(this.repository.findAll(), User.class);
	}

	/**
	 * 创建用户
	 * 
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> createUser(ServerRequest request) {
		// 2.0.0 是可以工作, 但是2.0.1 下面这个模式是会报异常
		// User user = request.bodyToMono(User.class).block()

		Mono<User> user = request.bodyToMono(User.class);

		return user.flatMap(u -> {
			// 校验代码需要放在这里,且自定义异常ExceptionHandler
			CheckUtil.checkName(u.getName());
			
			return ok().contentType(MediaType.APPLICATION_JSON_UTF8)
					.body(this.repository.save(u), User.class);
		});
	}

	/**
	 * 根据id删除用户
	 * 
	 * @param request
	 * @return
	 */
	public Mono<ServerResponse> deleteUserById(ServerRequest request) {
		String id = request.pathVariable("id");

		return this.repository.findById(id)
				.flatMap(
						user -> this.repository.delete(user).then(ServerResponse.ok().build()))
				.switchIfEmpty(ServerResponse.notFound().build());
	}

}
