package io.tinyrpc.demo.spring.xml.consumer;

import io.tinyrpc.common.utils.ThreadUtil;
import io.tinyrpc.consumer.RpcClient;
import io.tinyrpc.demo.api.DemoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:spring-consumer.xml"})
public class SpringXmlConsumerMain {

	private static final Logger logger = LoggerFactory.getLogger(SpringXmlConsumerMain.class);

	@Autowired
	private RpcClient rpcClient;

	@Test
	public void testInterfaceRpc() {
		DemoService demoService = rpcClient.create(DemoService.class);

		long start = System.currentTimeMillis();
		String result = demoService.hello("world");
		logger.info(">>> Request return: {}, timing: {}ms", result, (System.currentTimeMillis() - start));

		ThreadUtil.sleep(60, TimeUnit.SECONDS);
	}
}
