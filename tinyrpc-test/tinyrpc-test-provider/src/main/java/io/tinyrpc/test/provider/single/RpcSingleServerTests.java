package io.tinyrpc.test.provider.single;

import io.tinyrpc.provider.RpcSingleServer;
import org.junit.jupiter.api.Test;

public class RpcSingleServerTests {

	@Test
	public void testStartRpcSingleServer() {
		RpcSingleServer singleServer = new RpcSingleServer(
			"127.0.0.1:27880",
			"127.0.0.1:2181",
			"zookeeper",
			"enhanced_leastconnections",
			"io.tinyrpc.test",
			"asm",
			30000,
			60000,
			true,
			5000,
			5,
			10,
			"print",
			10,
			"strategy_default",
			false,
			100,
			true,
			"counter",
			10,
			1000,
			"exception",
			true,
			"counter",
			10D,
			1000,
			"print"
		);

		singleServer.startNettyServer();
	}
}
