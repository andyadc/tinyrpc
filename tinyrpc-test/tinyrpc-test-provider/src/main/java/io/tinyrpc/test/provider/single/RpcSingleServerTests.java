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
			"io.tinyrpc.test",
			"javassist");

		singleServer.startNettyServer();
	}
}
