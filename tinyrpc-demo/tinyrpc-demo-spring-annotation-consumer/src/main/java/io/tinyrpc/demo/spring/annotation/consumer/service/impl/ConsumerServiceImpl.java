package io.tinyrpc.demo.spring.annotation.consumer.service.impl;

import io.tinyrpc.annotation.RpcReference;
import io.tinyrpc.demo.api.DemoService;
import io.tinyrpc.demo.spring.annotation.consumer.service.ConsumerService;
import org.springframework.stereotype.Service;

@Service
public class ConsumerServiceImpl implements ConsumerService {

	@RpcReference(
		registryType = "zookeeper",
		registryAddress = "127.0.0.1:2181",
		loadBalanceType = "zkconsistenthash",
		version = "1.0.0",
		group = "adc",
		serializationType = "protostuff",
		proxy = "cglib",
		timeout = 30000,
		async = false,
		oneway = false
	)
	private DemoService demoService;

	@Override
	public String hello(String message) {
		return demoService.hello(message);
	}
}