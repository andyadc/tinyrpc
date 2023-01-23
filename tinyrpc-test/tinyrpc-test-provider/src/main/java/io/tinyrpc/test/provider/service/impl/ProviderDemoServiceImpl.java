package io.tinyrpc.test.provider.service.impl;

import io.tinyrpc.annotation.RpcService;
import io.tinyrpc.test.provider.service.DemoService;

@RpcService(
	interfaceClass = DemoService.class,
	interfaceClassName = "",
	version = "1.0.0",
	group = "adc"
)
public class ProviderDemoServiceImpl implements DemoService {
}
