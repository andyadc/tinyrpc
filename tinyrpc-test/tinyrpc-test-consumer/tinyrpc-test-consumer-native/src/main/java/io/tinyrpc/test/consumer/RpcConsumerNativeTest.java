package io.tinyrpc.test.consumer;

import io.tinyrpc.consumer.RpcClient;
import io.tinyrpc.test.api.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试Java原生启动服务消费者
 */
public class RpcConsumerNativeTest {

	private static final Logger logger = LoggerFactory.getLogger(RpcConsumerNativeTest.class);

	public static void main(String[] args) {
		RpcClient rpcClient = new RpcClient("1.0.0", "g-1", "jdk", 3000, false, false);

		TestService service = rpcClient.create(TestService.class);
		String result = service.hello("andyadc");
		logger.info("返回的结果数据 ===>>> " + result);

		rpcClient.shutdown();
	}
}
