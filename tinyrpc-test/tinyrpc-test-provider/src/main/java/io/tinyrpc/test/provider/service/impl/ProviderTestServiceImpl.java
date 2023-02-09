package io.tinyrpc.test.provider.service.impl;

import io.tinyrpc.annotation.RpcService;
import io.tinyrpc.test.api.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RpcService(
	interfaceClass = TestService.class,
	interfaceClassName = "io.tinyrpc.test.api.TestService",
	version = "1.0.0",
	group = "g-1"
)
public class ProviderTestServiceImpl implements TestService {

	private static final Logger logger = LoggerFactory.getLogger(ProviderTestServiceImpl.class);

	@Override
	public String hello(String name) {
		logger.info("Call method [hello] params ===>>> {}", name);
		return "hello " + name;
	}
}
