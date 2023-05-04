package io.tinyrpc.demo.spring.annotation.provider;

import io.tinyrpc.demo.spring.annotation.provider.config.SpringAnnotationProviderConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 基于Spring注解的服务提供者启动类
 */
public class SpringAnnotationProviderMain {

	public static void main(String[] args) {
		new AnnotationConfigApplicationContext(SpringAnnotationProviderConfig.class);
	}
}
