package io.tinyrpc.common.test.service;

import io.tinyrpc.annotation.RpcService;

@RpcService(
	interfaceClass = HelloService.class,
	interfaceClassName = "io.tinyrpc.common.test.service.HelloService",
	version = "2.0.0",
	group = "tiny-g-1"
)
public class HelloServiceImpl {
}
