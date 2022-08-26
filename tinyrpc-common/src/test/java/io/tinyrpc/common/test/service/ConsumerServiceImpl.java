package io.tinyrpc.common.test.service;

import io.tinyrpc.annotation.RpcReference;

public class ConsumerServiceImpl implements ConsumerService {

	@RpcReference(
		version = "2.0.0",
		group = "tiny-g-1"
	)
	private HelloService helloService;
}
