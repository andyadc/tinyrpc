package io.tinyrpc.test.provider;

import io.tinyrpc.annotation.RpcService;
import io.tinyrpc.test.service.HelloService;

@RpcService(
	interfaceClass = HelloService.class,
	interfaceClassName = "io.tinyrpc.test.service.HelloService",
	version = "1.0.0",
	group = "tiny-g-1"
)
public class HelloProviderServiceImpl implements HelloService {
}
