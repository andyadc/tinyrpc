package io.tinyrpc.demo.provider.service;

import io.tinyrpc.annotation.RpcService;
import io.tinyrpc.common.exception.RpcException;
import io.tinyrpc.demo.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RpcService(
	interfaceClass = DemoService.class,
	interfaceClassName = "io.tinyrpc.demo.api.DemoService",
	version = "1.0.0",
	group = "adc",
	weight = 3
)
public class ProviderNativeDemoService implements DemoService {

	private static final Logger logger = LoggerFactory.getLogger(ProviderNativeDemoService.class);

	@Override
	public String hello(String message) {
		logger.info("===>>> request: {}", message);
		if ("java".equals(message)) {
			throw new RpcException("message cannot equal to 'java'");
		}
		return "hello " + message + " - " + System.currentTimeMillis();
	}
}
