package io.tinyrpc.demo.spring.annotation.consumer;

import io.tinyrpc.demo.spring.annotation.consumer.config.SpringAnnotationConsumerConfig;
import io.tinyrpc.demo.spring.annotation.consumer.service.ConsumerService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 基于Spring注解的消费者测试类
 */
public class SpringAnnotationConsumerMain {

	private static final Logger logger = LoggerFactory.getLogger(SpringAnnotationConsumerMain.class);

	@Test
	public void testInterfaceRpc() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringAnnotationConsumerConfig.class);
		ConsumerService consumerService = context.getBean(ConsumerService.class);
		String result = consumerService.hello("consumer");

		logger.info("===>>> request return: {}", result);
	}
}
