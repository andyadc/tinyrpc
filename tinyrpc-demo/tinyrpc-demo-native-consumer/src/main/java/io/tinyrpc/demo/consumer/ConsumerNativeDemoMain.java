package io.tinyrpc.demo.consumer;

import io.tinyrpc.common.utils.ThreadUtil;
import io.tinyrpc.consumer.RpcClient;
import io.tinyrpc.demo.api.DemoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ConsumerNativeDemoMain {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerNativeDemoMain.class);

	private RpcClient rpcClient;

	@BeforeEach
	public void init() {
		rpcClient = new RpcClient(
			"127.0.0.1:2181",
			"zookeeper",
			"enhanced_leastconnections",
			"asm",
			"1.0.0",
			"adc",
			"protostuff",
			3000,
			false,
			false,
			30000,
			60000,
			1000,
			3);
	}

	@Test
	public void testInterfaceRpc() {
		DemoService demoService = rpcClient.create(DemoService.class);

		long start = System.currentTimeMillis();
		String result = demoService.hello("world");
		logger.info(">>> Request return: {}, timing: {}ms", result, (System.currentTimeMillis() - start));

		ThreadUtil.sleep(60, TimeUnit.SECONDS);

		rpcClient.shutdown();
	}
}