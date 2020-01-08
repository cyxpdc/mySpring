package com.pdc;

import com.pdc.interfaces.ProxyCreator;
import com.pdc.proxys.JDKProxyCreator;
import com.pdc.test.IUserApi;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author pdc
 */
@SpringBootApplication
public class WebfluxclientApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxclientApplication.class, args);
	}

	/**
	 * 注册jdk代理工具类
	 * bug：没有注册的话，报错没有找到这个类
	 * @return
	 */
	@Bean
	public ProxyCreator jdkProxyCreator() {
		return new JDKProxyCreator();
	}

	/**
	 * 会自动查询已经注册了的bean中是否有ProxyCreator的实现类
	 * 如果有ProxyCreator的多个实现类，则需要使用@Qualifier指定方法
	 * @param proxyCreator
	 * @return
	 */
	@Bean
	FactoryBean<IUserApi> userApi(ProxyCreator proxyCreator) {
		return new FactoryBean<IUserApi>() {
			@Override
			public Class<?> getObjectType() {
				return IUserApi.class;
			}
			/**
			 * 返回代理对象
			 * 输入为接口信息，即其名字，输出为对象
			 */
			@Override
			public IUserApi getObject() throws Exception {
				return (IUserApi) proxyCreator
						.createProxy(this.getObjectType());
			}
		};
	}
}
