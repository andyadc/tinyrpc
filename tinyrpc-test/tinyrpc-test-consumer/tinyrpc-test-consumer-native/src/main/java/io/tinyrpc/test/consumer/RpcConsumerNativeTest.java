package io.tinyrpc.test.consumer;

import io.tinyrpc.consumer.RpcClient;
import io.tinyrpc.proxy.api.async.IAsyncObjectProxy;
import io.tinyrpc.proxy.api.future.RPCFuture;
import io.tinyrpc.test.api.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试Java原生启动服务消费者
 */
public class RpcConsumerNativeTest {

	private static final Logger logger = LoggerFactory.getLogger(RpcConsumerNativeTest.class);

	public static void main(String[] args) throws Exception {
		RpcClient rpcClient = new RpcClient("127.0.0.1:2181", "zookeeper","1.0.0", "g-1", "jdk", 3000, false, false);

		IAsyncObjectProxy testService = rpcClient.createAsync(TestService.class);
		RPCFuture rpcFuture = testService.call("hello", "adc");
		logger.info("返回的结果数据 ===>>> " + rpcFuture.get());

		rpcClient.shutdown();
	}

	private RpcClient rpcClient;

	@BeforeEach
	public void init() {
		rpcClient = new RpcClient(
			"127.0.0.1:2181",
			"zookeeper",
			"1.0.0",
			"g-1",
			"json",
			3000,
			false,
			false);
	}

	@Test
	public void testInterfaceRpc() {
		TestService service = rpcClient.create(TestService.class);
		String result = service.hello("andyadc");
		logger.info("返回的结果数据 ===>>> " + result);

		rpcClient.shutdown();
	}

	@Test
	public void testAsyncInterfaceRpc() throws Exception {
		IAsyncObjectProxy testService = rpcClient.createAsync(TestService.class);
		RPCFuture rpcFuture = testService.call("hello", "adc");
		logger.info("返回的结果数据 ===>>> " + rpcFuture.get());

		rpcClient.shutdown();
	}
}
