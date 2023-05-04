package io.tinyrpc.demo.spring.xml.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务提供者启动类
 */
public class SpringXmlProviderMain {

	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring-provider.xml");
	}
}
