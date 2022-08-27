package io.tinyrpc.test.consumer;

import io.tinyrpc.annotation.RpcReference;
import io.tinyrpc.test.service.HelloService;

public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {

	@RpcReference(
		registryType = "zookeeper",
		registryAddress = "127.0.0.1:2181",
		version = "1.0.0",
		group = "tiny-g-1"
	)
	private HelloService helloService;

}
