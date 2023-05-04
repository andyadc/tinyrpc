package io.tinyrpc.demo.spring.annotation.provider.service;

import io.tinyrpc.annotation.RpcService;
import io.tinyrpc.demo.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@RpcService(
	interfaceClass = DemoService.class,
	interfaceClassName = "io.tinyrpc.demo.api.DemoService",
	version = "1.0.0",
	group = "adc",
	weight = 5
)
public class ProviderDemoService implements DemoService {

	private static final Logger logger = LoggerFactory.getLogger(ProviderDemoService.class);

	@Override
	public String hello(String message) {
		logger.info("===>>> request: {}", message);

		return "hello " + message + " - " + System.currentTimeMillis();
	}
}
