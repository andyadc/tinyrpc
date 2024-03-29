package io.tinyrpc.test.consumer;

import io.tinyrpc.common.utils.ThreadUtil;
import io.tinyrpc.consumer.RpcClient;
import io.tinyrpc.proxy.api.async.IAsyncObjectProxy;
import io.tinyrpc.proxy.api.future.RPCFuture;
import io.tinyrpc.test.api.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 测试Java原生启动服务消费者
 */
public class RpcConsumerNativeTest {

	private static final Logger logger = LoggerFactory.getLogger(RpcConsumerNativeTest.class);
	private RpcClient rpcClient;

	public static void main(String[] args) throws Exception {
		RpcClient rpcClient = new RpcClient(
			"127.0.0.1:2181", "zookeeper", "random",
			"jdk", "1.0.0", "g-1", "jdk",
			3000, false, false,
			30000, 60000, 1000, 3, false, 500,
			false, "", 2,
			3,
			"print",
			true,
			2,
			"jdk",
			"io.tinyrpc.demo.consumer.FallbackDemoServcie",
			false,
			"guava",
			1,
			5000,
			"exception",
			true,
			"counter",
			1D,
			5000,
			"print"
		);

		IAsyncObjectProxy testService = rpcClient.createAsync(TestService.class);
		RPCFuture rpcFuture = testService.call("hello", "adc");
		logger.info("返回的结果数据 ===>>> " + rpcFuture.get());

		rpcClient.shutdown();
	}

	@BeforeEach
	public void init() {
		rpcClient = new RpcClient(
			"127.0.0.1:2181",
			"zookeeper",
			"enhanced_leastconnections",
			"asm",
			"1.0.0",
			"g-1",
			"protostuff",
			3000,
			false,
			false,
			30000,
			60000,
			1000,
			3,
			false,
			5000,
			false,
			"", 2,
			3,
			"print",
			true,
			2,
			"jdk",
			"io.tinyrpc.demo.consumer.FallbackDemoServcie",
			false,
			"guava",
			1,
			5000,
			"exception",
			true,
			"counter",
			1D,
			5000,
			"print"
		);
	}

	@Test
	public void testInterfaceRpc() {
		TestService service = rpcClient.create(TestService.class);
		String result = service.hello("andyadc");
		logger.info("返回的结果数据 ===>>> " + result);

		ThreadUtil.sleep(60, TimeUnit.SECONDS);

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
