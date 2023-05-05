package io.tinyrpc.demo.spring.boot.consumer;

import io.tinyrpc.demo.spring.boot.consumer.service.ConsumerDemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"io.tinyrpc"})
public class SpringBootConsumerDemoMain {

	private static final Logger logger = LoggerFactory.getLogger(SpringBootConsumerDemoMain.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringBootConsumerDemoMain.class, args);

		ConsumerDemoService service = context.getBean(ConsumerDemoService.class);
		String result = service.hello("adc");
		logger.info("===>>> request return: {}", result);
	}
}
